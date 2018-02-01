
/*
* Extract a metadata type from an existing package xml
*/
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ExtractMetadataTypesFromPackageXMLTask extends DefaultTask {
    String metadataMapFilePath
    String inputPackageXMLFilePath
    String outputPackageXMLFilePath
    String metadataTypesFilePath
    String apiVersion

    @TaskAction
    def extract() {
        File inputPackageXMLFile = new File(inputPackageXMLFilePath);
    	File metadataMapFile = new File(metadataMapFilePath);
        File metadataTypesFile = new File(metadataTypesFilePath);
    	def inputPackageXMLContent = new groovy.util.XmlSlurper().parseText(inputPackageXMLFile.getText());
    	def metadataMapContent = new groovy.json.JsonSlurper().parseText(metadataMapFile.text); 
        def metadataMap = new MetadataMap(metadataMapContent);
        def mapResult = extractMetadataTypes(metadataMap,inputPackageXMLContent,metadataTypesFile);
        def packageXML = generatePackageXML(mapResult)
        println "Package xml generated : \n $packageXML"
    }

    // Extract 
    def extractMetadataTypes(metadataMap,packageXMLContent,metadataTypesFile) {
        def result = [];

        def metadataTypeNameList = []

        metadataTypesFile.each{ metadataTypeName -> 
            metadataTypeName = metadataTypeName.trim();

            def metadataType = metadataMap.getMetadataTypeByName(metadataTypeName)
            metadataTypeNameList.add(metadataType.name);
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
    def generatePackageXML(xmlMetadataTypes){

        def sw = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(sw)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

        xml.Package(xmlns:"http://soap.sforce.com/2006/04/metadata"){
            xmlMetadataTypes.each({ type ->
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


