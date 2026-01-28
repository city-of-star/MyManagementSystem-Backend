package com.mms.usercenter.service.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.UserAssignDeptDto;
import com.mms.usercenter.common.auth.entity.RoleEntity;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.org.dto.*;
import com.mms.usercenter.common.org.entity.DeptEntity;
import com.mms.usercenter.common.org.entity.UserDeptEntity;
import com.mms.usercenter.common.org.vo.DeptVo;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import com.mms.usercenter.service.org.mapper.DeptMapper;
import com.mms.usercenter.service.org.mapper.UserDeptMapper;
import com.mms.usercenter.service.org.service.DeptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实现功能【部门服务实现类】
 * <p>
 * 提供部门管理的核心业务逻辑实现
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:33:45
 */
@Slf4j
@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DeptMapper deptMapper;

    @Resource
    private UserDeptMapper userDeptMapper;

    @Override
    public Page<DeptVo> getDeptPage(DeptPageQueryDto dto) {
        try {
            log.info("分页查询部门列表，参数：{}", dto);
            Page<DeptVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return deptMapper.getDeptPage(page, dto);
        } catch (Exception e) {
            log.error("分页查询部门列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询部门列表失败", e);
        }
    }

    @Override
    public DeptVo getDeptById(Long deptId) {
        try {
            log.info("根据ID查询部门，deptId：{}", deptId);
            if (deptId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门ID不能为空");
            }
            DeptEntity dept = deptMapper.selectById(deptId);
            if (dept == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门不存在");
            }
            return convertToVo(dept);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据ID查询部门失败：{}", e.getMessage(), e);
            throw new ServerException("查询部门失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeptVo createDept(DeptCreateDto dto) {
        try {
            log.info("创建部门，参数：{}", dto);
            // 检查部门编码是否存在
            if (existsByDeptCode(dto.getDeptCode())) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门编码已存在");
            }
            // 创建部门实体
            DeptEntity dept = new DeptEntity();
            BeanUtils.copyProperties(dto, dept);
            // 设置默认值
            if (dept.getStatus() == null) {
                dept.setStatus(1);
            }
            if (dept.getSortOrder() == null) {
                dept.setSortOrder(0);
            }
            dept.setDeleted(0);
            // 保存部门
            deptMapper.insert(dept);
            log.info("创建部门成功，deptId：{}", dept.getId());
            return convertToVo(dept);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建部门失败：{}", e.getMessage(), e);
            throw new ServerException("创建部门失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeptVo updateDept(DeptUpdateDto dto) {
        try {
            log.info("更新部门信息，参数：{}", dto);
            // 查询部门
            DeptEntity dept = deptMapper.selectById(dto.getId());
            if (dept == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门不存在");
            }
            // 更新字段
            if (dto.getParentId() != null) {
                dept.setParentId(dto.getParentId());
            }
            if (StringUtils.hasText(dto.getDeptName())) {
                dept.setDeptName(dto.getDeptName());
            }
            if (StringUtils.hasText(dto.getLeader())) {
                dept.setLeader(dto.getLeader());
            }
            if (StringUtils.hasText(dto.getPhone())) {
                dept.setPhone(dto.getPhone());
            }
            if (StringUtils.hasText(dto.getEmail())) {
                dept.setEmail(dto.getEmail());
            }
            if (dto.getSortOrder() != null) {
                dept.setSortOrder(dto.getSortOrder());
            }
            if (StringUtils.hasText(dto.getRemark())) {
                dept.setRemark(dto.getRemark());
            }
            deptMapper.updateById(dept);
            log.info("更新部门信息成功，deptId：{}", dept.getId());
            return convertToVo(dept);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新部门信息失败：{}", e.getMessage(), e);
            throw new ServerException("更新部门信息失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDept(Long deptId) {
        try {
            log.info("删除部门，deptId：{}", deptId);
            if (deptId == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门ID不能为空");
            }
            // 查询部门
            DeptEntity dept = deptMapper.selectById(deptId);
            if (dept == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门不存在");
            }
            // 检查是否有子部门
            LambdaQueryWrapper<DeptEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeptEntity::getParentId, deptId)
                    .eq(DeptEntity::getDeleted, 0);
            if (deptMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "该部门下存在子部门，无法删除");
            }
            // 逻辑删除
            deptMapper.deleteById(deptId);
            log.info("删除部门成功，deptId：{}", deptId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除部门失败：{}", e.getMessage(), e);
            throw new ServerException("删除部门失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDept(DeptBatchDeleteDto dto) {
        try {
            log.info("批量删除部门，deptIds：{}", dto.getDeptIds());
            if (dto.getDeptIds() == null || dto.getDeptIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门ID列表不能为空");
            }
            // 批量逻辑删除
            for (Long deptId : dto.getDeptIds()) {
                deleteDept(deptId);
            }
            log.info("批量删除部门成功，删除数量：{}", dto.getDeptIds().size());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除部门失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除部门失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchDeptStatus(DeptStatusSwitchDto dto) {
        try {
            log.info("切换部门状态，deptId：{}，status：{}", dto.getDeptId(), dto.getStatus());
            DeptEntity dept = deptMapper.selectById(dto.getDeptId());
            if (dept == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门不存在");
            }
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "状态值只能是0或1");
            }
            dept.setStatus(dto.getStatus());
            deptMapper.updateById(dept);
            log.info("切换部门状态成功，deptId：{}，status：{}", dto.getDeptId(), dto.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("切换部门状态失败：{}", e.getMessage(), e);
            throw new ServerException("切换部门状态失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDepts(UserAssignDeptDto dto) {
        try {
            log.info("为用户分配部门，userId：{}，deptIds：{}", dto.getUserId(), dto.getDeptIds());
            if (dto.getUserId() == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "用户ID不能为空");
            }
            UserEntity user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }
            saveUserDepts(dto.getUserId(), dto.getDeptIds(), dto.getPrimaryDeptId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("分配用户部门失败：{}", e.getMessage(), e);
            throw new ServerException("分配用户部门失败", e);
        }
    }

    @Override
    public List<Long> listDeptIdsByUserId(Long userId) {
        try {
            log.info("查询用户部门ID列表，userId：{}", userId);
            LambdaQueryWrapper<UserDeptEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserDeptEntity::getUserId, userId);
            return userDeptMapper.selectList(wrapper).stream()
                    .map(UserDeptEntity::getDeptId)
                    .toList();
        } catch (Exception e) {
            log.error("查询用户部门ID列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户部门ID列表失败", e);
        }
    }

    @Override
    public List<DeptVo> getDeptListByUserId(Long userId) {
        try {
            log.info("查询用户部门信息列表，userId：{}", userId);
            // 先获取部门ID列表
            List<Long> deptIds = listDeptIdsByUserId(userId);
            if (CollectionUtils.isEmpty(deptIds)) {
                return new ArrayList<>();
            }
            // 批量查询部门实体
            List<DeptEntity> depts = deptMapper.selectBatchIds(deptIds);
            // 创建ID到实体的映射
            Map<Long, DeptEntity> deptMap = depts.stream()
                    .collect(Collectors.toMap(DeptEntity::getId, dept -> dept));
            // 按照原始ID列表的顺序转换为VO
            return deptIds.stream()
                    .map(deptMap::get)
                    .filter(Objects::nonNull)
                    .map(this::convertToVo)
                    .toList();
        } catch (Exception e) {
            log.error("查询用户部门信息列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户部门信息列表失败", e);
        }
    }

    @Override
    public DeptVo getPrimaryDeptByUserId(Long userId) {
        try {
            log.info("查询用户主部门信息，userId：{}", userId);
            return deptMapper.getPrimaryDeptByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户主部门信息失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户主部门信息失败", e);
        }
    }

    // ==================== 私有工具方法 ====================

    /**
     * 保存用户部门关联（覆盖）
     */
    private void saveUserDepts(Long userId, List<Long> deptIds, Long primaryDeptId) {
        // 先清空旧关联
        LambdaQueryWrapper<UserDeptEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDeptEntity::getUserId, userId);
        userDeptMapper.delete(wrapper);

        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }

        // 校验部门是否存在且未删除、已启用
        List<DeptEntity> depts = deptMapper.selectBatchIds(deptIds);
        if (depts.size() != deptIds.size()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "存在无效的部门ID");
        }
        for (DeptEntity dept : depts) {
            if (Objects.equals(dept.getDeleted(), 1)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门 " + dept.getDeptName() + " 已被删除");
            }
            if (Objects.equals(dept.getStatus(), 0)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "部门 " + dept.getDeptName() + " 已被禁用，无法分配");
            }
        }

        // 主部门校验
        if (primaryDeptId != null && !deptIds.contains(primaryDeptId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "主部门ID必须在部门ID列表中");
        }

        List<Long> distinctIds = deptIds.stream().distinct().toList();
        for (Long deptId : distinctIds) {
            UserDeptEntity entity = new UserDeptEntity();
            entity.setUserId(userId);
            entity.setDeptId(deptId);
            entity.setIsPrimary(primaryDeptId != null && Objects.equals(primaryDeptId, deptId) ? 1 : 0);
            entity.setCreateTime(LocalDateTime.now());
            userDeptMapper.insert(entity);
        }
    }

    /**
     * 判断部门编码是否存在
     */
    private boolean existsByDeptCode(String deptCode) {
        if (!StringUtils.hasText(deptCode)) {
            return false;
        }
        LambdaQueryWrapper<DeptEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeptEntity::getDeptCode, deptCode)
                .eq(DeptEntity::getDeleted, 0);
        return deptMapper.selectCount(wrapper) > 0;
    }

    // ==================== 实体转换方法 ====================

    /**
     * 将 DeptEntity 转换为 DeptVo
     *
     * @param entity 部门实体
     * @return 部门VO
     */
    private DeptVo convertToVo(DeptEntity entity) {
        if (entity == null) {
            return null;
        }
        DeptVo vo = new DeptVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}