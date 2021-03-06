package com.zkyyo.www.serve;

import com.zkyyo.www.dao.DepartmentDao;
import com.zkyyo.www.po.DepartmentPo;
import com.zkyyo.www.po.EmployeePo;
import com.zkyyo.www.util.CreateIdUtil;
import com.zkyyo.www.util.QueryUtil;
import com.zkyyo.www.util.ScannerUtil;
import com.zkyyo.www.view.DepartmentView;

import java.util.ArrayList;

public class DepartmentServe {
    private static volatile DepartmentServe INSTANCE = null;

    private DepartmentServe() {}

    public static DepartmentServe getInstance() {
        if (INSTANCE == null) {
            synchronized (DepartmentServe.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DepartmentServe();
                }
            }
        }
        return INSTANCE;
    }

    public void addDept(EmployeePo handler) {
        DepartmentDao dd = DepartmentDao.getInstance();
        DepartmentPo newDept = new DepartmentPo();
        boolean isAdded = false;
        System.out.println("开始建立新的部门,请输入必要的信息");

        System.out.println("部门号(必选):");
        int newDeptId = CreateIdUtil.creatDepartmentId();
        newDept.setDeptId(newDeptId);
        System.out.println("部门名(必选):");
        String newDeptName = ScannerUtil.scanString(true);
        newDept.setDeptName(newDeptName);
        System.out.println("部门描述(可选,默认为无):");
        String newDesc = ScannerUtil.scanString(false);
        newDept.setDeptDesc(newDesc);
        System.out.println("建立日期(可选,默认为当前日期):");
        java.sql.Date newBuiltDate = ScannerUtil.scanSqlDate();
        newDept.setBuiltDate(newBuiltDate);

        isAdded = dd.addDepartment(newDept);
        if (isAdded) {
            System.out.println("建立部门成功");
        } else {
            System.out.println("建立部门失败");
        }
        DepartmentView.departmentManage(handler);
    }

    public void deleteDept(EmployeePo handler) {
        System.out.println("请输入需要删除的部门Id");
        int deletedDeptId = ScannerUtil.scanNum();

        DepartmentDao dd = DepartmentDao.getInstance();
        DepartmentPo deletedDept = dd.selectDepartmentByDeptId(deletedDeptId);
        if (deletedDept == null) {
            System.out.println("该部门不存在");
            DepartmentView.departmentManage(handler);
        } else {
            System.out.println("信息如下");
            System.out.println("部门号: " + deletedDept.getDeptId());
            System.out.println("部门名: " + deletedDept.getDeptName());
            System.out.println("部门人数: " + deletedDept.getDeptPopulation());
            System.out.println("部门描述: " + deletedDept.getDeptDesc());
            System.out.println("建立时间: " + deletedDept.getBuiltDate());

            do {
                System.out.println("是否删除该部门[y/n]:");
                String choice = ScannerUtil.scanString(true);
                String firstLetter = choice.substring(0, 1);

                if (firstLetter.equalsIgnoreCase("y")) {
                    boolean isDeleted = dd.deleteDept(deletedDeptId);
                    if (isDeleted) {
                        System.out.println("你已成功删除该部门");
                    } else {
                        System.out.println("删除部门操作失败");
                    }
                    DepartmentView.departmentManage(handler);
                } else if (firstLetter.equalsIgnoreCase("n")) {
                    DepartmentView.departmentManage(handler);
                } else {
                    System.out.println("请输入正确的选项:");
                }
            } while (true);
        }
    }

    public void updateDept(EmployeePo handler) {
        DepartmentDao dd = DepartmentDao.getInstance();
        boolean isUpdate = false;

        System.out.println("请输入需要修改的部门号:");
        int updatedDeptId = ScannerUtil.scanNum();
        DepartmentPo foundDept = dd.selectDepartmentByDeptId(updatedDeptId);
        if (foundDept == null) {
            System.out.println("查无此部门");
        } else {
            System.out.println("信息如下");
            System.out.println("部门号: " + foundDept.getDeptId());
            System.out.println("部门名: " + foundDept.getDeptName());
            System.out.println("部门人数: " + foundDept.getDeptPopulation());
            System.out.println("部门描述: " + foundDept.getDeptDesc());
            System.out.println("建立时间: " + foundDept.getBuiltDate());
            System.out.println();
            System.out.println("1. 部门名");
            System.out.println("2. 部门描述");
            System.out.println("3. 建立时间");

            do {
                System.out.println("请输入待修改的选项(0返回部门信息管理):");
                int choice = ScannerUtil.scanNum();
                switch (choice) {
                    //返回 部门信息管理
                    case 0:
                        DepartmentView.departmentManage(handler);
                        break;
                    //部门名
                    case 1:
                        System.out.println("请输入修改后的部门名(必选):");
                        String newDeptName = ScannerUtil.scanString(true);
                        foundDept.setDeptName(newDeptName);
                        isUpdate = dd.updateDept(updatedDeptId, 1, foundDept);
                        break;
                    //部门描述
                    case 2:
                        System.out.println("请输入修改后的部门描述(可选):");
                        String newDeptDesc = ScannerUtil.scanString(false);
                        foundDept.setDeptDesc(newDeptDesc);
                        isUpdate = dd.updateDept(updatedDeptId, 2, foundDept);
                        break;
                    //建立时间
                    case 3:
                        System.out.println("请输入修改后的部门建立时间(选择跳过即设置为当前时期):");
                        java.sql.Date newBuiltDate = ScannerUtil.scanSqlDate();
                        foundDept.setBuiltDate(newBuiltDate);
                        isUpdate = dd.updateDept(updatedDeptId, 3, foundDept);
                        break;
                    default:
                        System.out.println("无效选项,请重新输入:");
                        break;
                }
                if (isUpdate) {
                    System.out.println("修改成功,继续修改或0返回部门信息管理:");
                } else {
                    System.out.println("修改失败,正在返回部门信息管理");
                    DepartmentView.departmentManage(handler);
                }
            } while (true);
        }
    }

    public void queryDepartment(int type, EmployeePo handler) {
        int possibleUserId;
        int possibleDeptId;
        int accurateDeptId;
        String possibleUserName;
        String possibleDeptName;
        EmployeePo foundEpy = null;
        DepartmentPo foundDept = null;
        DepartmentDao dd = DepartmentDao.getInstance();

        switch (type) {
            case 1:
                System.out.println("请输入需要精确查询的部门号:");
                accurateDeptId = ScannerUtil.scanNum();
                QueryUtil.queryDepartmentByDeptrId(accurateDeptId);
                break;
            case 2:
                System.out.println("请输入需要模糊查询的部门号");
                possibleDeptId = ScannerUtil.scanNum();
                foundDept = QueryUtil.fuzzyQueryDepartment(possibleDeptId);
                if (foundDept != null) {
                    QueryUtil.queryDepartmentByDeptrId(foundDept.getDeptId());
                }
                break;
            case 3:
                System.out.println("请输入需要模糊查询的部门名");
                possibleDeptName = ScannerUtil.scanString(true);
                foundDept = QueryUtil.fuzzyQueryDepartment(possibleDeptName);
                if (foundDept != null) {
                    QueryUtil.queryDepartmentByDeptrId(foundDept.getDeptId());
                }
                break;
            case 4:
                System.out.println("请输入需要模糊查询的员工号");
                possibleUserId = ScannerUtil.scanNum();
                foundEpy = QueryUtil.fuzzyQueryEmployee(possibleUserId);
                if (foundEpy != null) {
                    QueryUtil.queryDepartmentByDeptrId(foundEpy.geteDeptId());
                }
                break;
            case 5:
                System.out.println("请输入需要模糊查询的员工名");
                possibleUserName = ScannerUtil.scanString(true);
                foundEpy = QueryUtil.fuzzyQueryEmployee(possibleUserName);
                if (foundEpy != null) {
                    QueryUtil.queryDepartmentByDeptrId(foundEpy.geteDeptId());
                }
                break;
            case 6:
                ArrayList<DepartmentPo> depts = dd.selectDepartments();
                if (depts.size() == 0) {
                    System.out.println("找不到任何部门");
                } else {
                    for (DepartmentPo dept : depts) {
                        System.out.printf("部门号:%-6d 部门名:%-12s 人数:%-6d 建立时间:%-16s 部门描述:%s\n",
                                dept.getDeptId(), dept.getDeptName(), dept.getDeptPopulation(),
                                dept.getBuiltDate(), dept.getDeptDesc());
                    }
                }
                break;
            default:
                break;
        }
        DepartmentView.departmentManage(handler);
    }

}
