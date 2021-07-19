package importer;

import importer.model.CellularRoute;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class ImporterService {


    public void insertList(List list) {

        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "";
        sql += "INSERT IGNORE INTO mobi_org";
        sql += "(";
        sql += "    dt,";
        sql += "    path_id,";
        sql += "    ride_stn_nm,";
        sql += "    ride_tm,";
        sql += "    start_line_nm,";
        sql += "    line_route,";
        sql += "    trnsf_route,";
        sql += "    line_trip_time,";
        sql += "    algh_stn_nm,";
        sql += "    algh_tm,";
        sql += "    end_line_nm,";
        sql += "    psng_num";
        sql += ")";
        sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Class.forName("org.mariadb.jdbc.Driver");
            // 설정파일(properties) 경로
            String path = Paths.get("").toAbsolutePath().toString() + File.separator + "conf" + File.separator + "db.properties";
            System.out.println("DB Properties: " + path);

            FileReader reader = new FileReader(path);
            Properties properties = new Properties();
            properties.load(reader);

            // 데이터파일 경로
            String url = properties.getProperty("URL");
            String username = properties.getProperty("USERNAME");
            String password = properties.getProperty("PASSWORD");
            conn = DriverManager.getConnection(url, username, password);

            // 첫번째 데이터 기준, 해당 일자 데이터 사전 삭제
            String preSql = "DELETE FROM MOBI_ORG WHERE DT = ?";
            CellularRoute stdrData = (CellularRoute) list.get(0);
            pstmt = conn.prepareStatement(preSql);
            pstmt.setString(1, stdrData.getDt());
            pstmt.executeUpdate();
            conn.commit();

            pstmt = conn.prepareStatement(sql);

            int loopLimit = list.size();
            for (int i=0; i<loopLimit; i++) {
                CellularRoute cr = (CellularRoute) list.get(i);
                pstmt.setString(1, cr.getDt());
                pstmt.setInt(2, cr.getPathId());
                pstmt.setString(3, cr.getRideStnNm());
                pstmt.setString(4, cr.getRideTm());
                pstmt.setString(5, cr.getStartLineNm());
                pstmt.setString(6, cr.getLineRoute());
                pstmt.setString(7, cr.getTrnsfRoute());
                pstmt.setString(8, cr.getLineTripTime());
                pstmt.setString(9, cr.getEndLineNm());
                pstmt.setString(10, cr.getAlghTm());
                pstmt.setString(11, cr.getAlghStnNm());
                pstmt.setString(12, cr.getPsngNum());

                System.out.println("PATH_ID..." + cr.getPathId() + " add Batch");
                pstmt.addBatch();

                pstmt.clearParameters();

                if ((i % 3000) == 0) {
                    System.out.println("execute Batch...insert");
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    conn.commit();
                }

            }

            System.out.println("execute Batch...insert");
            pstmt.executeBatch();
            conn.commit();

            System.out.println("insert complete!");

        } catch (Exception e) {
            e.printStackTrace();

            try {
                System.out.println("error...rollback!");
                conn.rollback() ;
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }finally{
            System.out.println("connection close");
            if (pstmt != null) try {pstmt.close();pstmt = null;} catch(SQLException ex){}
            if (conn != null) try {conn.close();conn = null;} catch(SQLException ex){}
        }

    }

}
