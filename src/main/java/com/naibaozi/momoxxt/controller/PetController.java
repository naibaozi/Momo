/**
 * @Author: j.c.zong 1258899660@qq.com
 * @Date: 2025-09-19 15:33:35
 * @LastEditors: j.c.zong 1258899660@qq.com
 * @LastEditTime: 2025-09-23 08:41:39
 * @FilePath: src/main/java/com/naibaozi/momoxxt/controller/PetController.java
 * @Description: 宠物信息管理控制器（增删改查、默认宠物设置等）
 * Copyright (c) 2025 by j.c.zong 1258899660@qq.com, All Rights Reserved. 
 */
package com.naibaozi.momoxxt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.naibaozi.momoxxt.dao.DictPetTypeDao;
import com.naibaozi.momoxxt.dao.UserPetDao;
import com.naibaozi.momoxxt.entity.DictPetType;
import com.naibaozi.momoxxt.entity.UserPet;
import com.naibaozi.momoxxt.util.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@WebServlet("/pet/*")
public class PetController extends HttpServlet {

    // 注入UserPetDao
    @Resource
    private UserPetDao userPetDao;

    // 注入DictPetTypeDao
    @Resource
    private DictPetTypeDao dictPetTypeDao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应内容类型和编码
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取操作参数
        String op = request.getParameter("op");

        try {
            // 根据操作类型处理请求
            if (op != null) {
                switch (op) {
                    case "getPets":
                        getUserPetsByUserId(request, response, out);
                        break;
                    case "getPet":
                        getUserPetById(request, response, out);
                        break;
                    case "getAllPetTypes":  // 新增
                        getAllPetTypes(request, response, out);
                        break;
                    case "getPetTypesByParentId":  // 新增
                        getPetTypesByParentId(request, response, out);
                        break;
                    default:
                        out.write(ResultUtil.error(400, "无效的操作类型"));
                }
            } else {
                out.write(ResultUtil.error(400, "请指定操作类型(op参数)"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "服务器内部错误: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String op = request.getParameter("op");

        try {
            if (op != null) {
                switch (op) {
                    case "addPet":
                        addUserPet(request, response, out);
                        break;
                    case "updatePet":
                        updateUserPet(request, response, out);
                        break;
                    case "deletePet":
                        deleteUserPet(request, response, out);
                        break;
                    case "setDefaultPet":
                        setDefaultPet(request, response, out);
                        break;
                    default:
                        out.write(ResultUtil.error(400, "无效的操作类型"));
                }
            } else {
                out.write(ResultUtil.error(400, "请指定操作类型(op参数)"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "服务器内部错误: " + e.getMessage()));
        }
    }


    /**
     * 新增宠物
     */
    private void addUserPet(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. Token验证：获取当前登录用户ID
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请先登录"));
                return;
            }

            // 2. 解析请求参数
            JSONObject param = JSON.parseObject(request.getReader().readLine());
            UserPet userPet = new UserPet();
            // 强制绑定当前登录用户ID，禁止传入他人ID
            userPet.setUserId(currentUserId);
            userPet.setPetName(param.getString("petName"));
            userPet.setPetTypeId(param.getInteger("petTypeId"));
            userPet.setPetAge(param.getString("petAge"));
            userPet.setPetGender(param.getInteger("petGender"));
            userPet.setMedicalHistory(param.getString("medicalHistory"));
            userPet.setIsDefault(param.getInteger("isDefault"));

            // 3. 参数校验
            if (userPet.getPetName() == null || userPet.getPetTypeId() == null) {
                out.write(ResultUtil.error(400, "宠物名称和品类ID为必填项"));
                return;
            }

            // 4. 执行新增
            int petId = userPetDao.addUserPet(userPet);
            if (petId > 0) {
                if (userPet.getIsDefault() == 1) {
                    userPetDao.setDefaultPet(currentUserId, (long) petId);
                }
                Map<String, Object> data = new HashMap<>();
                data.put("petId", petId);
                out.write(ResultUtil.success(data));
            } else {
                out.write(ResultUtil.error(500, "宠物添加失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "添加宠物异常: " + e.getMessage()));
        }
    }

    /**
     * 删除宠物（修复参数获取方式）
     */
    private void deleteUserPet(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. 从JSON请求体中解析参数（关键修复）
            JSONObject param = JSON.parseObject(request.getReader().readLine());
            String petIdStr = param.getString("petId"); // 从JSON中获取petId

            System.out.println("从JSON中获取的petId: " + petIdStr);
            if (petIdStr == null || petIdStr.trim().isEmpty()) {
                out.write(ResultUtil.error(400, "宠物ID不能为空"));
                return;
            }

            // 2. Token验证：获取当前登录用户ID
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请先登录"));
                return;
            }

            // 3. 归属权校验：仅允许删除自己的宠物
            Long petId = Long.parseLong(petIdStr);
            UserPet pet = userPetDao.getUserPetById(petId);
            if (pet == null) {
                out.write(ResultUtil.error(404, "宠物不存在或已删除"));
                return;
            }
            if (!pet.getUserId().equals(currentUserId)) {
                out.write(ResultUtil.error(403, "权限不足，无法删除他人宠物"));
                return;
            }

            // 4. 执行删除
            int rows = userPetDao.deleteUserPet(petId);
            if (rows > 0) {
                out.write(ResultUtil.success("宠物删除成功"));
            } else {
                out.write(ResultUtil.error(404, "宠物不存在或已删除"));
            }
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "宠物ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "删除宠物异常: " + e.getMessage()));
        }
    }

    /**
     * 更新宠物信息
     */
    private void updateUserPet(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. Token验证：获取当前登录用户ID
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请先登录"));
                return;
            }

            // 2. 解析请求参数
            JSONObject param = JSON.parseObject(request.getReader().readLine());
            UserPet userPet = new UserPet();
            userPet.setId(param.getLong("id"));
            userPet.setPetName(param.getString("petName"));
            userPet.setPetTypeId(param.getInteger("petTypeId"));
            userPet.setPetAge(param.getString("petAge"));
            userPet.setPetGender(param.getInteger("petGender"));
            userPet.setMedicalHistory(param.getString("medicalHistory"));
            userPet.setIsDefault(param.getInteger("isDefault"));

            // 3. 基础参数校验
            if (userPet.getId() == null) {
                out.write(ResultUtil.error(400, "宠物ID不能为空"));
                return;
            }

            // 4. 归属权校验：仅允许更新自己的宠物
            UserPet oldPet = userPetDao.getUserPetById(userPet.getId());
            if (oldPet == null) {
                out.write(ResultUtil.error(404, "宠物不存在"));
                return;
            }
            if (!oldPet.getUserId().equals(currentUserId)) {
                out.write(ResultUtil.error(403, "权限不足，无法修改他人宠物"));
                return;
            }

            // 5. 执行更新
            int rows = userPetDao.updateUserPet(userPet);
            if (userPet.getIsDefault() != null && userPet.getIsDefault() == 1) {
                userPetDao.setDefaultPet(currentUserId, userPet.getId());
            }

            if (rows > 0) {
                out.write(ResultUtil.success("宠物信息更新成功"));
            } else {
                out.write(ResultUtil.error(404, "宠物不存在或未修改"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "更新宠物异常: " + e.getMessage()));
        }
    }

    /**
     * 根据ID查询宠物
     */
    private void getUserPetById(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String petIdStr = request.getParameter("petId");
        if (petIdStr == null || petIdStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "宠物ID不能为空"));
            return;
        }

        try {
            // 1. Token验证：获取当前登录用户ID
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请先登录"));
                return;
            }

            // 2. 查询宠物并校验归属权
            Long petId = Long.parseLong(petIdStr);
            UserPet pet = userPetDao.getUserPetById(petId);
            if (pet == null) {
                out.write(ResultUtil.error(404, "宠物不存在"));
                return;
            }

            // 3. 权限校验：仅允许查询自己的宠物
            if (!pet.getUserId().equals(currentUserId)) {
                out.write(ResultUtil.error(403, "权限不足，无法查看他人宠物"));
                return;
            }

            // 4. 返回结果
            out.write(ResultUtil.success(pet));
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "宠物ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "查询宠物异常: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID查询宠物列表
     */
    private void getUserPetsByUserId(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            out.write(ResultUtil.error(400, "用户ID不能为空"));
            return;
        }

        try {
            // 1. Token验证：获取当前登录用户ID（拦截器已校验Token有效性）
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请先登录"));
                return;
            }

            // 2. 权限校验：仅允许查询自己的宠物列表
            Long targetUserId = Long.parseLong(userIdStr);
            if (!currentUserId.equals(targetUserId)) {
                out.write(ResultUtil.error(403, "权限不足，仅能查看自己的宠物"));
                return;
            }

            // 3. 执行查询
            List<UserPet> pets = userPetDao.getUserPetsByUserId(targetUserId);
            Map<String, Object> data = new HashMap<>();
            data.put("data", pets);
            data.put("count", pets.size());
            out.write(ResultUtil.success(data));
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "用户ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "查询宠物列表异常: " + e.getMessage()));
        }
    }

    /**
     * 设置默认宠物
     */
    private void setDefaultPet(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. 从JSON请求体中解析参数（关键修复）
            JSONObject param = JSON.parseObject(request.getReader().readLine());
            String userIdStr = param.getString("userId");
            String petIdStr = param.getString("petId"); // 从JSON中获取petId

            if (userIdStr == null || petIdStr == null) {
                out.write(ResultUtil.error(400, "用户ID和宠物ID不能为空"));
                return;
            }

            // 1. Token验证：获取当前登录用户ID
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            if (currentUserId == null) {
                out.write(ResultUtil.error(401, "未登录，请先登录"));
                return;
            }

            // 2. 权限校验：仅允许设置自己的宠物为默认
            Long targetUserId = Long.parseLong(userIdStr);
            Long petId = Long.parseLong(petIdStr);
            if (!currentUserId.equals(targetUserId)) {
                out.write(ResultUtil.error(403, "权限不足，仅能设置自己的宠物为默认"));
                return;
            }

            // 3. 校验宠物归属权
            UserPet pet = userPetDao.getUserPetById(petId);
            if (pet == null || !pet.getUserId().equals(currentUserId)) {
                out.write(ResultUtil.error(404, "设置失败，宠物不存在或不属于您"));
                return;
            }

            // 4. 执行设置
            int rows = userPetDao.setDefaultPet(currentUserId, petId);
            if (rows > 0) {
                out.write(ResultUtil.success("默认宠物设置成功"));
            } else {
                out.write(ResultUtil.error(404, "设置失败，宠物不存在"));
            }
        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "设置默认宠物异常: " + e.getMessage()));
        }
    }

    /**
     * 获取所有宠物品类（支持一级和二级品类层级）
     * 前端调用：/pet?op=getAllPetTypes
     */
    private void getAllPetTypes(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. 查询所有品类
            List<DictPetType> allTypes = dictPetTypeDao.getAllPetTypes();

            // 2. 构建层级结构（一级品类包含二级品类）
            List<DictPetType> result = new ArrayList<>();
            Map<Integer, List<DictPetType>> childrenMap = new HashMap<>();

            // 2.1 先分离一级品类和二级品类
            for (DictPetType type : allTypes) {
                if (type.getParentId() == 0) {
                    // 一级品类直接加入结果集
                    result.add(type);
                } else {
                    // 二级品类按父ID分组
                    childrenMap.computeIfAbsent(type.getParentId(), k -> new ArrayList<>())
                            .add(type);
                }
            }

            // 2.2 为一级品类添加子品类
            for (DictPetType parent : result) {
                List<DictPetType> children = childrenMap.get(parent.getId());
                if (children != null) {
                    // 这里需要在DictPetType实体类中添加children字段及getter/setter
                    parent.setChildren(children);
                }
            }

            // 3. 返回结果
            out.write(ResultUtil.success(result));

        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "查询宠物品类失败: " + e.getMessage()));
        }
    }

    /**
     * 根据父ID获取子品类
     * 前端调用：/pet?op=getPetTypesByParentId&parentId=xxx
     */
    private void getPetTypesByParentId(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            // 1. 获取参数
            String parentIdStr = request.getParameter("parentId");
            if (parentIdStr == null || parentIdStr.trim().isEmpty()) {
                out.write(ResultUtil.error(400, "parentId参数不能为空"));
                return;
            }
            Integer parentId = Integer.parseInt(parentIdStr);

            // 2. 调用Dao查询
            List<DictPetType> petTypes = dictPetTypeDao.getPetTypesByParentId(parentId);

            // 3. 返回结果
            out.write(ResultUtil.success(petTypes));

        } catch (NumberFormatException e) {
            out.write(ResultUtil.error(400, "parentId格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            out.write(ResultUtil.error(500, "查询子品类失败: " + e.getMessage()));
        }
    }
}