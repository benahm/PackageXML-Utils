
/*
* Extract a metadata type from an existing package xml
*/
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ExtractMetadataTypeFromPackageXMLTask extends DefaultTask {
    String metadataMapFilePath
    String inputPackageXMLFilePath
    String outputPackageXMLFilePath
    List metadataTypeNames
    String apiVersion

    @TaskAction
    def extract() {
        File packageXMLFile = new File(inputPackageXMLFilePath);
    	File metadataMapFile = new File(metadataMapFilePath);
    	def packageXMLContent = new groovy.util.XmlSlurper().parseText(packageXMLFile.getText());
    	def metadataMap = new groovy.json.JsonSlurper().parseText(metadataMapFile.text); 
        def mapResult = extractMetadataType(metadataMap,packageXMLContent,metadataTypeNames);
        def packgeXML = generatePackageXML(mapResult)
        println "Package xml generated : \n $packgeXML"
    }

    def getMetadataTypeByFileSuffix(metadataMap,fileSuffix){
        return metadataMap.find({ it.fileSuffix == fileSuffix});
    }

    def getMetadataTypeByDirectoryName(metadataMap,directoryName){
        return metadataMap.find({ it.directoryName == directoryName});
    }

    def getMetadataTypeByName(metadataMap,name){
        return metadataMap.find({ it.name == name});
    }

    // Extract 
    def extractMetadataType(metadataMap,packageXMLContent,metadataTypeNames) {
        def result = [];

        def metadataTypeNameList = []

        metadataTypeNames.each{ metadataTypeName -> 
            def metadataType = getMetadataTypeByName(metadataMap,metadataTypeName)
            
            metadataTypeNameList.add(metadataType.name);

            if(metadataType.dependences != null){ // meta with dependences
                metadataType.dependences.each { name ->
                    metadataTypeNameList.add(name);
                }
            }
        }

        // loop through the file packageXMLContent
        packageXMLContent.types.each { type ->
            if(metadataTypeNameList.contains(type.name)){
                result.add(type);
            }
        }
        return result;
    }

    // Generate
    def generatePackageXML(xmlMetdataTypes){

        def sw = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(sw)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

        xml.Package(xmlns:"http://soap.sforce.com/2006/04/metadata"){
            xmlMetdataTypes.each({ type ->
                types(){
                    type.members.each { member ->
                        members(member)
                    }
                    name(type.name)
                }
            })
            __version__(apiVersion)
        }

        File file = new File(outputPackageXMLFilePath)

        def packageXMLContent = sw.toString().replace('__version__','version');

        file.write packageXMLContent

        return packageXMLContent
    }
}


