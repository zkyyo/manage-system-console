package com.zkyyo.www.util;

import com.zkyyo.www.db.DbClose;
import com.zkyyo.www.db.DbConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateIdUtil {
    public static final int userIdDigits = 10;

    public static int creatUserId() {
        Connection conn = DbConn.getConn();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int largestNum = (int) Math.pow(10, userIdDigits);

        try {
            String sql = "SELECT * FROM employee WHERE user_id=?";
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < largestNum; i++) {
                int newUserId = (int) (largestNum * Math.random());
                if (newUserId != 0) {
                    stmt.setInt(1, newUserId);
                    rs = stmt.executeQuery();
                    if (!rs.next()) {
                        return newUserId;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbClose.close(conn, stmt, rs);
        }
        return 0;
    }

    public static int creatDepartmentId() {
        Connection conn = DbConn.getConn();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            do {
                int newDeptId = ScannerUtil.scanNum();
                if (newDeptId >= 1000) {
                    System.err.println("部门号超出范围,请重新输入");
                } else {
                    String sql = "SELECT * FROM department WHERE dept_id=?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, newDeptId);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.err.println("该部门号已经被注册,请重新输入");
                    } else {
                        return newDeptId;
                    }
                }
            } while (true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbClose.close(conn, stmt, rs);
        }
        return 0;
    }

    /**
     * 单元测试
     * @param args
     */
    public static void main(String[] args) {
        do {
            int deptId = creatDepartmentId();
            System.out.println(deptId);
        } while (true);
    }
}