
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
    	File metadataMapFile = new File(metadataMapFilePath);
        File inputPackageXMLFile = new File(inputPackageXMLFilePath);
        File outputPackageXMLFile = new File(outputPackageXMLFilePath)
        File metadataTypesFile = new File(metadataTypesFilePath);
    	def inputPackageXMLContent = new groovy.util.XmlSlurper().parseText(inputPackageXMLFile.getText());
    	def metadataMapContent = new groovy.json.JsonSlurper().parseText(metadataMapFile.text); 
        def metadataMap = new MetadataMap(metadataMapContent);
        def xmlMetadataTypes = extractMetadataTypes(metadataMap,inputPackageXMLContent,metadataTypesFile);
        def packageXML = new PackageXML(xmlMetadataTypes,apiVersion);
        def packageXMLContent = packageXML.generate(outputPackageXMLFile)
        println "Package xml generated : \n $packageXMLContent"
    }

    // Extract 
    def extractMetadataTypes(metadataMap,packageXMLContent,metadataTypesFile) {
        def xmlMetadataTypes = [];

        def metadataTypeNameList = []

        metadataTypesFile.each{ metadataTypeName -> 
            metadataTypeName = metadataTypeName.trim();

            def metadataType = metadataMap.getMetadataTypeByName(metadataTypeName)
            metadataTypeNameList.add(metadataType.name);
        }

        // loop through the file packageXMLContent
        packageXMLContent.types.each { type ->
            if(metadataTypeNameList.contains(type.name)){
                xmlMetadataTypes.add(type);
            }
        }
        return xmlMetadataTypes;
    }

}


