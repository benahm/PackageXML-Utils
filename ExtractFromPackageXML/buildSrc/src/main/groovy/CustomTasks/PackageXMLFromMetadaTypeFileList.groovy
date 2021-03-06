

/*
* Generate a package xml from a list of metadata types for retrieve
*/
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PackageXMLFromMetadaTypeFileListTask extends DefaultTask {
    String metadataMapFilePath
    String inputContentListFilePath
    String outputPackageXMLFilePath
    String apiVersion

    @TaskAction
    def generate() {
        File metadataMapFile = new File(metadataMapFilePath);
        File inputContentListFile = new File(inputContentListFilePath);
        File outputPackageXMLFile = new File(outputPackageXMLFilePath)
        def metadataMapContent = new groovy.json.JsonSlurper().parseText(metadataMapFile.text); 
        def metadataMap = new MetadataMap(metadataMapContent);
        def xmlMetadataTypes = parseFileContent(metadataMap,inputContentListFile);
        def packageXML = new PackageXML(xmlMetadataTypes,apiVersion);
        def packageXMLContent = packageXML.generate(outputPackageXMLFile)
        println "Package xml generated : \n $packageXMLContent"
    }

    // Parse 
    def parseFileContent(metadataMap,contentList) {
        def xmlMetadataTypeMap = [:];

        // loop through the file contentList
        contentList.eachLine { fileName, count ->
            fileName = fileName.trim();

            if(!fileName.contains('.')) throw new ParseException("Error parsing the file at line ${count+1} : $fileName must have a suffix");
            def fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1) // TODO add try/catch
            def name = fileName.substring(0,fileName.lastIndexOf("."))

            def metadataType = metadataMap.getMetadataTypeByFileSuffix(fileSuffix)
            if(metadataType != null){
                if(metadataType.inFolder == "true"){ // with folder
                    if(!name.contains('/'))
                        throw new ParseException("Error parsing the file at line ${count+1} : $fileName must have a folder");
                }

                if(xmlMetadataTypeMap[metadataType.name] == null) xmlMetadataTypeMap[metadataType.name]=[name:metadataType.name,members:[]]
                xmlMetadataTypeMap[metadataType.name].members.add(name)
            }else throw new ParseException("Error parsing the file at line ${count+1} : $fileName is invalid");

        }
        return xmlMetadataTypeMap.values();
    }

}