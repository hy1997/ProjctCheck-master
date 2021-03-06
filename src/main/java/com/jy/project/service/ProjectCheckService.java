package com.jy.project.service;


import com.alibaba.excel.EasyExcel;
import com.jy.project.mapper.ProjectCheckMapper;
import com.jy.project.mapper.ProjectInfoMapper;
import com.jy.project.model.ProjectInfoModel;
import com.jy.project.po.ProjectInfoPO;
import com.jy.project.po.ProjectPO;
import com.jy.project.utils.JsonUtil;
import com.jy.project.utils.UrlUtils;
import com.jy.project.vo.ProjectInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectCheckService {

    @Autowired
    ProjectCheckMapper projectCheckMapper;

    @Autowired
    ProjectInfoMapper projectInfoMapper;

    @Value("${environment.url}")
    private String environmentUrl;

    @Value("${environment.historicalAirQuality}")
    private String historicalAirQuality;

    @Value("${environment.getAll}")
    private String getAll;


    @Value("${realname.personGoInTodayList}")
    private String personGoInTodayList;

    @Value("${realname.todayTeamAttendance}")
    private String todayTeamAttendance;

    @Value("${realname.typeOfWorkStatistics}")
    private String typeOfWorkStatistics;

    @Value("${realname.ageDistribution}")
    private String ageDistribution;

    @Value("${realname.todayAttendance}")
    private String todayAttendance;


    public Integer add(ProjectPO po) {
        return projectCheckMapper.insert(po);
    }

    public String projectCheck(ProjectPO po) throws Exception {
        List<ProjectPO> projectPOS = projectCheckMapper.selectList(null);
        for (ProjectPO projectPO : projectPOS) {
            ProjectInfoPO infoPo = new ProjectInfoPO();
            infoPo.setProjectId(projectPO.getId());
            infoPo.setCreatetime(new Date());
            if ("是".equals(projectPO.getIsCheck())) {
                if (StringUtils.isNoneBlank(projectPO.getProjectCode())) {
                    StringBuilder sb = new StringBuilder();
                    if ("1".equals(projectPO.getEnvironmentSurveillance())) {
                        //环境接口调用
                        boolean environmentAllflg = true;
                        Map<String, Object> environmentAll = JsonUtil.jsonToMap(UrlUtils.sendPost(environmentUrl, String.format(historicalAirQuality, projectPO.getProjectCode())));
                        if (Integer.parseInt(environmentAll.get("code").toString()) != 200 && checkObject(environmentAll.get("data"))) {
                            environmentAllflg = false;
                        }
                        //环境
                        Map<String, Object> environmentMap = JsonUtil.jsonToMap(UrlUtils.sendPost(environmentUrl, String.format(getAll, projectPO.getProjectCode(), LocalDate.now())));
                        if (Integer.parseInt(environmentMap.get("code").toString()) != 200 && checkObject(environmentMap.get("data"))) {
                            environmentAllflg = false;
                        }
                        if (environmentAllflg) {
                            //环境接口正常
                            infoPo.setEnvironmentSurveillanceInfo("正常");
                        } else {
                            sb.append("环境检测接口异常");
                            infoPo.setEnvironmentSurveillanceInfo("异常");
                        }
                    } else {
                        infoPo.setEnvironmentSurveillanceInfo("不用检测");
                    }
                    //实名制接口检查
                    if ("1".equals(projectPO.getRealNameSurveillance())) {
                        boolean realanameflg = true;
                        Map<String, Object> stringObjectMap = JsonUtil.jsonToMap(UrlUtils.sendPost(environmentUrl, String.format(todayAttendance, LocalDate.now(), projectPO.getProjectCode())));
                        if (Integer.parseInt(stringObjectMap.get("code").toString()) != 200 && checkObject(stringObjectMap.get("data"))) {
                            realanameflg = false;
                        } else {
                            List<LinkedHashMap<String, Object>> datas = (List<LinkedHashMap<String, Object>>) stringObjectMap.get("data");
                            for (LinkedHashMap<String, Object> data : datas) {
                                if ("全部".equals(data.get("corpType"))) {
                                    Integer onJobNum = Integer.parseInt(data.get("onJobNum").toString());
                                    Integer attendanceNum = Integer.parseInt(data.get("attendanceNum").toString());
                                    Integer presentNum = Integer.parseInt(data.get("presentNum").toString());
                                    if (onJobNum == 0 || attendanceNum == 0 || presentNum == 0) {
                                        realanameflg = false;
                                    }
                                }
                            }
                        }
                        if (realanameflg) {
                            infoPo.setRealNameSurveillanceInfo("正常");
                        } else {
                            sb.append("实名制接口异常");
                            infoPo.setRealNameSurveillanceInfo("异常");
                        }
                        infoPo.setCause(sb.toString());
                    } else {
                        infoPo.setRealNameSurveillanceInfo("不用检测");
                    }
                    projectInfoMapper.insert(infoPo);
                }
            }else{
                infoPo.setCause("该项目接口信息不需要检查！");
                projectInfoMapper.insert(infoPo);
            }
        }
        return "1";
    }

    public List<ProjectPO> projectList(ProjectPO po) {
        List<ProjectPO> projectPOS = projectCheckMapper.selectList(null);
        return projectPOS;
    }

    public List<ProjectInfoVO> projectInfoList(ProjectInfoPO po) {
        List<ProjectInfoVO> list = projectInfoMapper.list(po);
        list.sort((m1, m2) -> m2.getCreatetime().compareTo(m1.getCreatetime()));
        return list;

    }


    public boolean checkObject(Object obj) {

        if (obj instanceof String) {
            if (StringUtils.isBlank(obj.toString())) {
                return true;
            }
        } else if (obj instanceof List) {
            if (((List<?>) obj).size() <= 0) {
                return true;
            }
        } else if (obj instanceof Map) {
            if (((Map<?, ?>) obj).size() <= 0) {
                return true;
            }
        }
        return false;
    }


    public void excel() {
        //欲导出excel的数据结果集
        List<ProjectInfoModel> excel = new ArrayList<>();
        //省略 向结果集里插入数据的操作
        List<ProjectInfoVO> list = projectInfoMapper.list(new ProjectInfoPO());
        BeanUtils.copyProperties(list, excel);
        //UUID生成唯一name
        String name = UUID.randomUUID().toString().replaceAll("-", "") + ".xlsx";
        //实现excel写的操作
        //1 设置写入文件夹地址和excel文件名称
        String filename = "/路径" + name;
        try {
            // 2 调用easyexcel里面的方法实现写操作
            // write方法两个参数：第一个参数文件路径名称，第二个参数实体类class
            EasyExcel.write(filename, ProjectInfoModel.class).sheet("名字").doWrite(excel);
//            //上传到fastdfs上 不上传的话只有本机可以找到，在上面路径下生成excel
//            File file = new File(filename);
//            String path = fastDFSClient.upload(new FileInputStream(file), name, null);
//            path = (this.fastdfsDomain + path);
//            json.put("url", path);
        } finally {
            new File(filename).delete();
        }
    }
}