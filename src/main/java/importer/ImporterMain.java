package importer;

import importer.model.CellularRoute;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.*;
import java.lang.ref.Cleaner;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.text.SimpleDateFormat;

import utils.CommonUtils;
import utils.CommonUtils.*;

public class ImporterMain {

    public static void main(String[] args) throws Exception {

        ImporterService is = new ImporterService();

        // 기준 디렉토리
        Path stdrPath = Paths.get("");
        System.out.println("Working Directory = " + stdrPath);

        // 설정파일(properties) 경로
        String path = stdrPath.toAbsolutePath().toString() + File.separator + "conf" + File.separator + "props.properties";
        System.out.println("Config: " + path);

        FileReader reader = new FileReader(path);
        Properties properties = new Properties();
        properties.load(reader);

        // 데이터파일 경로
        String MOBILE_DATA_PATH = properties.getProperty("MOBILE_DATA_PATH");
        System.out.println("Data Directory: " + MOBILE_DATA_PATH);

        // 디렉토리 전체 용량
        System.out.println("전체 용량 : " +  FileUtils.sizeOfDirectory(new File(MOBILE_DATA_PATH)) + "Byte");

        List<String> PATH_LIST = new ArrayList<>();

        // 하위의 모든 파일
        System.out.println("INSERT TARGET ... ");
        for (File info : FileUtils.listFiles(new File(MOBILE_DATA_PATH), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            if ((info.getName()).indexOf("korail") > -1) {
                PATH_LIST.add(MOBILE_DATA_PATH + "/" + info.getName());
            }
        }

        System.out.println("[FILE_LIST]");
        for (String p : PATH_LIST) {
            System.out.println(p);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        long startTime = System.currentTimeMillis();
        System.out.print("START TIME: ");
        System.out.println(sdf.format(startTime));

        try {


            for (String filePath : PATH_LIST) {

                // CSV Read
                List<CellularRoute> cellularRouteList = new ArrayList<>();

                CSVFormat csvFormat = CSVFormat.RFC4180.withHeader().withDelimiter(',');
                CSVParser csvParser = CSVParser.parse(new File(filePath), Charset.forName("EUC-KR"), csvFormat);

                System.out.println(filePath);
                List<CSVRecord> csvRecordList = csvParser.getRecords();

                for (int i=0; i<csvRecordList.size(); i++) {
                    CSVRecord record = csvRecordList.get(i);

                    CellularRoute cellularRoute = new CellularRoute();
                    cellularRoute.setPathId(i + 1);

                    for (int j=0; j<record.size(); j++) {
                        String cell = record.get(j);

                        switch (j) {
                            case 0:
                                cellularRoute.setRideStnNm(cell);
                                break;
                            case 1:
                                cellularRoute.setRideTm(cell);
                                cellularRoute.setDt(validateDate(cell));
                                break;
                            case 2:
                                cellularRoute.setStartLineNm(cell);
                                break;
                            case 3:
                                cellularRoute.setLineRoute(cell);
                                break;
                            case 4:
                                cellularRoute.setTrnsfRoute(cell);
                                break;
                            case 5:
                                cellularRoute.setLineTripTime(cell);
                                break;
                            case 6:
                                cellularRoute.setAlghStnNm(cell);
                                break;
                            case 7:
                                cellularRoute.setAlghTm(cell);
                                break;
                            case 8:
                                cellularRoute.setEndLineNm(cell);
                                break;
                            case 9:
                                cellularRoute.setPsngNum(cell);
                                break;
                        } // switch end

                    }

                    cellularRouteList.add(cellularRoute);
                }

                is.insertList(cellularRouteList);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.print("END TIME: ");
        System.out.println(sdf.format(endTime));

        long diff = (endTime - startTime) / 1000;
        System.out.println("수행시간: " + diff + " sec");
    }

    public static String validateDate(String cell) throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date originDate = sf.parse(cell);
        String dateStr = sf.format(sf.parse(cell));
        String hhmmss = dateStr.split(" ")[1];
        int hh = Integer.parseInt(hhmmss.split(":")[0]);

        String convDate = dateStr;

        // 3시 이전 데이터의 경우, DT 맞춰줌
        if (hh <= 3) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(originDate);
            cal.add(Calendar.DATE, -1);
            convDate = sf.format(cal.getTime());
        }

        String _dt = convDate.split(" ")[0];

        return _dt;
    }

}
