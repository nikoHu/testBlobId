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
    public static int getCurCommitCount(int groupId) throws SQLException {
        DruidPooledConnection connection = DruidUtil.getConnection();
        if (connection == null){
            throw new SQLException("get connection failed");
        }
        String sql = String.format("select count(*) as num from %s where groupId=%d and isCurCommit=1", Config.TABLE, groupId);
        PreparedStatement statement = connection.prepareStatement(sql);
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
