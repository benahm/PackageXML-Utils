


class MetadataMap {

    def metadataMap

    MetadataMap(metadataMap){
        this.metadataMap = metadataMap;
    }

    def getMetadataTypeByFileSuffix(fileSuffix){
        return metadataMap.find({ it.fileSuffix == fileSuffix});
    }

    def getMetadataTypeByDirectoryName(directoryName){
        return metadataMap.find({ it.directoryName == directoryName});
    }

    def getMetadataTypeByName(name){
        return metadataMap.find({ it.name == name});
    }
}