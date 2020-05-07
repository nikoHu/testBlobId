package com.fudan;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.fudan.config.Config;
import com.fudan.util.DruidUtil;
import com.fudan.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExtractCurCommitClone {
    public static void main(String[] args) throws IOException {
        Config.load();
        System.out.println("result path: " + Config.PROJECT + "\n");
        System.out.println("table Name: " + Config.TABLE + "\n");
        try {
            System.out.println("init druid data source...");
            DruidUtil.initDataSource();
        }catch (SQLException e){
            e.printStackTrace();
            System.exit(0);
        }
        List<String> result = new ArrayList<>();
        List<String> content = new ArrayList<>();
        try {
            content = FileUtil.readLines(Config.PROJECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < content.size(); i++){
            if(i == 0)
                continue;
            String line = content.get(i);
            String[] lineArr = line.split(",");
            try {
                System.out.println("groupId: " + lineArr[0] + " --- " + getCurCommitCount(Integer.parseInt(lineArr[0])));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                int curCount = getCurCommitCount(Integer.parseInt(lineArr[0]));
                if(curCount > 1){
                    result.add(lineArr[0] + "," + curCount);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        FileWriter fw = new FileWriter("extracted.csv");
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i = 0; i < result.size(); i++){
            bw.write(result.get(i));
            bw.newLine();
        }
        bw.close();
        DruidUtil.close();
        System.out.println("finish");
    }

    public static int getCurCommitCount(int groupId) throws SQLException {
        DruidPooledConnection connection = DruidUtil.getConnection();
        if (connection == null){
            throw new SQLException("get connection failed");
        }
        String sql = String.format("select count(*) as num from %s where groupId=%d and isCurCommit=1", Config.TABLE, groupId);
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        int count = 0;
        if (resultSet.next()){
            count = resultSet.getInt("num");
        }
        try {
            resultSet.close();
            statement.close();
        }finally {
            connection.recycle();
        }
        return count;
    }
}
