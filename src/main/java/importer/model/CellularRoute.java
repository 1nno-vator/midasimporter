package importer.model;

import lombok.Data;

@Data
public class CellularRoute {

    String dt;
    int pathId;
    String rideStnNm;
    String rideTm;
    String startLineNm;
    String lineRoute;
    String trnsfRoute;
    String lineTripTime;
    String alghStnNm;
    String alghTm;
    String endLineNm;
    String psngNum;

}
