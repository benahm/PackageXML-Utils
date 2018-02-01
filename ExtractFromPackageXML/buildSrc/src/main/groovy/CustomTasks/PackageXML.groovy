
/*
* package xml 
*/

class PackageXML {

    def xmlMetadataTypes
    def apiVersion

    PackageXML(xmlMetadataTypes,apiVersion){
        this.xmlMetadataTypes = xmlMetadataTypes
        this.apiVersion = apiVersion;
    }

    // Generate a package xml
    def generate(outputPackageXMLFile){

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

        def packageXMLContent = sw.toString().replace('__version__','version');

        outputPackageXMLFile.write packageXMLContent

        return packageXMLContent
    }
}