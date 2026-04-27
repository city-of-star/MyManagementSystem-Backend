package com.mms.usercenter.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.auth.dto.UserPreferenceBatchSaveDto;
import com.mms.usercenter.common.auth.dto.UserPreferenceSaveDto;
import com.mms.usercenter.common.auth.entity.UserPreferenceEntity;
import com.mms.usercenter.common.auth.vo.UserPreferenceVo;
import com.mms.usercenter.service.auth.mapper.UserPreferenceMapper;
import com.mms.usercenter.service.auth.service.UserPreferenceService;
import com.mms.usercenter.service.auth.utils.UserUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 实现功能【用户偏好配置服务实现类】
 * <p>
 * 提供当前登录用户偏好配置的查询与保存能力
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-27 15:00:00
 */
@Slf4j
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Resource
    private UserPreferenceMapper userPreferenceMapper;

    @Override
    public List<UserPreferenceVo> getMyPreferences() {
        try {
            Long userId = UserUtils.getUserId();
            return userPreferenceMapper.selectByUserId(userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询当前用户偏好配置列表失败：{}", e.getMessage(), e);
            throw new ServerException("查询用户偏好配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserPreferenceVo saveMyPreference(UserPreferenceSaveDto dto) {
        try {
            Long userId = UserUtils.getUserId();
            String prefKey = dto.getPrefKey().trim();
            String valueType = dto.getValueType().trim().toLowerCase();

            LambdaQueryWrapper<UserPreferenceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserPreferenceEntity::getUserId, userId)
                    .eq(UserPreferenceEntity::getPrefKey, prefKey);
            UserPreferenceEntity entity = userPreferenceMapper.selectOne(wrapper);

            if (entity == null) {
                entity = new UserPreferenceEntity();
                entity.setUserId(userId);
                entity.setPrefKey(prefKey);
                entity.setPrefValue(dto.getPrefValue());
                entity.setValueType(valueType);
                entity.setRemark(dto.getRemark());
                entity.setDeleted(0);
                userPreferenceMapper.insert(entity);
            } else {
                entity.setPrefValue(dto.getPrefValue());
                entity.setValueType(valueType);
                entity.setRemark(dto.getRemark());
                entity.setDeleted(0);
                userPreferenceMapper.updateById(entity);
            }

            return userPreferenceMapper.selectByUserId(userId).stream()
                    .filter(item -> prefKey.equals(item.getPrefKey()))
                    .findFirst()
                    .orElseThrow(() -> new ServerException("保存用户偏好配置失败"));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("保存当前用户偏好配置失败：{}", e.getMessage(), e);
            throw new ServerException("保存用户偏好配置失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserPreferenceVo> batchSaveMyPreferences(UserPreferenceBatchSaveDto dto) {
        try {
            for (UserPreferenceSaveDto item : dto.getPreferences()) {
                saveMyPreference(item);
            }
            return getMyPreferences();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量保存当前用户偏好配置失败：{}", e.getMessage(), e);
            throw new ServerException("批量保存用户偏好配置失败", e);
        }
    }
}
