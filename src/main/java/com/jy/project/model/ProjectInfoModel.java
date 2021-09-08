package com.jy.project.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.util.Date;

/**
 * 　* @className: ProjectInfoModel
 *
 * @description: 　* @author: lj
 * 　* @date: 2021/09/08 15:04
 */

@Data
public class ProjectInfoModel extends BaseRowModel {

    @ExcelProperty(value = {"项目信息"}, index = 0)
    private String id;

    @ExcelProperty(value = {"项目名称"}, index = 1)
    private String projectName;

    @ExcelProperty(value = {"项目CODE"}, index = 2)
    private String projectCode;

    @ExcelProperty(value = {"视频"}, index = 3)
    private String videoSurveillanceInfo;

    @ExcelProperty(value = {"实名制"}, index = 4)
    private String realNameSurveillanceInfo;

    @ExcelProperty(value = {"环境"}, index = 5)
    private String environmentSurveillanceInfo;

    @ExcelProperty(value = {"环境"}, index = 6)
    private String highFormworkSurveillanceInfo;

    @ExcelProperty(value = {"高支模"}, index = 7)
    private String tbmSurveillanceInfo;

    @ExcelProperty(value = {"备注"}, index = 8)
    private String remarks;

    @ExcelProperty(value = {"问题描述"}, index = 9)
    private String problemDescription;

    @ExcelProperty(value = {"原因"}, index = 10)
    private String cause;

    @ExcelProperty(value = {"创建时间"}, index = 11)
    private Date createtime;

}

