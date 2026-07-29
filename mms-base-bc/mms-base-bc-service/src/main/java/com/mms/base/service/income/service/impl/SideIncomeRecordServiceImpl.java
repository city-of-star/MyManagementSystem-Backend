package com.mms.base.service.income.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.income.dto.SideIncomeBatchDeleteDto;
import com.mms.base.common.income.dto.SideIncomeCreateDto;
import com.mms.base.common.income.dto.SideIncomePageQueryDto;
import com.mms.base.common.income.dto.SideIncomeUpdateDto;
import com.mms.base.common.income.entity.SideIncomeRecordEntity;
import com.mms.base.common.income.vo.SideIncomeDailyStatVo;
import com.mms.base.common.income.vo.SideIncomeRecordVo;
import com.mms.base.common.income.vo.SideIncomeSummaryVo;
import com.mms.base.service.income.mapper.SideIncomeRecordMapper;
import com.mms.base.service.income.service.SideIncomeRecordService;
import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 实现功能【副业收入记录服务实现类】
 *
 * @author li.hongyu
 * @date 2026-07-29
 */
@Slf4j
@Service
public class SideIncomeRecordServiceImpl implements SideIncomeRecordService {

    private static final Set<String> SOURCE_TYPES = Set.of("self", "partner", "other");
    private static final Set<String> STATUSES = Set.of("paid", "pending");

    @Resource
    private SideIncomeRecordMapper sideIncomeRecordMapper;

    @Override
    public Page<SideIncomeRecordVo> getSideIncomePage(SideIncomePageQueryDto dto) {
        try {
            log.info("分页查询副业收入，参数：{}", dto);
            Page<SideIncomeRecordVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            return sideIncomeRecordMapper.getSideIncomePage(page, dto);
        } catch (Exception e) {
            log.error("分页查询副业收入失败：{}", e.getMessage(), e);
            throw new ServerException("查询副业收入列表失败", e);
        }
    }

    @Override
    public SideIncomeRecordVo getById(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "记录ID不能为空");
            }
            SideIncomeRecordEntity entity = sideIncomeRecordMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "收入记录不存在");
            }
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询副业收入详情失败：{}", e.getMessage(), e);
            throw new ServerException("查询副业收入详情失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SideIncomeRecordVo create(SideIncomeCreateDto dto) {
        try {
            log.info("创建副业收入，参数：{}", dto);
            validateSourceAndStatus(dto.getSourceType(), dto.getStatus());
            SideIncomeRecordEntity entity = new SideIncomeRecordEntity();
            entity.setRecordDate(dto.getRecordDate());
            entity.setAmount(scaleMoney(dto.getAmount()));
            entity.setGrossAmount(scaleMoney(resolveGrossAmount(dto.getSourceType(), dto.getAmount(), dto.getGrossAmount())));
            entity.setSourceType(dto.getSourceType());
            entity.setStatus(dto.getStatus());
            entity.setNote(dto.getNote());
            sideIncomeRecordMapper.insert(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建副业收入失败：{}", e.getMessage(), e);
            throw new ServerException("创建副业收入失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SideIncomeRecordVo update(SideIncomeUpdateDto dto) {
        try {
            log.info("更新副业收入，参数：{}", dto);
            SideIncomeRecordEntity entity = sideIncomeRecordMapper.selectById(dto.getId());
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "收入记录不存在");
            }
            if (dto.getRecordDate() != null) {
                entity.setRecordDate(dto.getRecordDate());
            }
            if (dto.getAmount() != null) {
                entity.setAmount(scaleMoney(dto.getAmount()));
            }
            if (StringUtils.hasText(dto.getSourceType())) {
                validateSourceAndStatus(dto.getSourceType(), null);
                entity.setSourceType(dto.getSourceType());
            }
            if (StringUtils.hasText(dto.getStatus())) {
                validateSourceAndStatus(null, dto.getStatus());
                entity.setStatus(dto.getStatus());
            }
            if (dto.getGrossAmount() != null) {
                entity.setGrossAmount(scaleMoney(dto.getGrossAmount()));
            } else if (dto.getAmount() != null || StringUtils.hasText(dto.getSourceType())) {
                entity.setGrossAmount(scaleMoney(resolveGrossAmount(
                        entity.getSourceType(), entity.getAmount(), entity.getGrossAmount())));
            }
            if (dto.getNote() != null) {
                entity.setNote(dto.getNote());
            }
            sideIncomeRecordMapper.updateById(entity);
            return convertToVo(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新副业收入失败：{}", e.getMessage(), e);
            throw new ServerException("更新副业收入失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        try {
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "记录ID不能为空");
            }
            SideIncomeRecordEntity entity = sideIncomeRecordMapper.selectById(id);
            if (entity == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "收入记录不存在");
            }
            sideIncomeRecordMapper.deleteById(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除副业收入失败：{}", e.getMessage(), e);
            throw new ServerException("删除副业收入失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(SideIncomeBatchDeleteDto dto) {
        try {
            if (dto.getIds() == null || dto.getIds().isEmpty()) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "记录ID列表不能为空");
            }
            for (Long id : dto.getIds()) {
                delete(id);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量删除副业收入失败：{}", e.getMessage(), e);
            throw new ServerException("批量删除副业收入失败", e);
        }
    }

    @Override
    public SideIncomeSummaryVo getSummary() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate monthStart = today.withDayOfMonth(1);

            SideIncomeSummaryVo summary = new SideIncomeSummaryVo();
            summary.setTodayReceived(sumAmount(today, today, "paid"));
            summary.setMonthReceived(sumAmount(monthStart, today, "paid"));
            summary.setMonthTotal(sumAmount(monthStart, today, null));
            summary.setPendingAmount(sumAmount(null, null, "pending"));
            summary.setTotalReceived(sumAmount(null, null, "paid"));
            return summary;
        } catch (Exception e) {
            log.error("查询副业收入汇总失败：{}", e.getMessage(), e);
            throw new ServerException("查询副业收入汇总失败", e);
        }
    }

    @Override
    public List<SideIncomeDailyStatVo> getDailyStats(Integer days) {
        try {
            int range = days == null || days < 1 ? 30 : Math.min(days, 90);
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(range - 1L);
            List<SideIncomeDailyStatVo> stats = sideIncomeRecordMapper.listDailyStats(startDate, endDate);
            Map<LocalDate, SideIncomeDailyStatVo> map = stats.stream()
                    .collect(Collectors.toMap(SideIncomeDailyStatVo::getRecordDate, item -> item, (a, b) -> a));

            // 补齐无数据的日期，方便前端画图
            java.util.ArrayList<SideIncomeDailyStatVo> filled = new java.util.ArrayList<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                SideIncomeDailyStatVo item = map.get(date);
                if (item == null) {
                    item = new SideIncomeDailyStatVo();
                    item.setRecordDate(date);
                    item.setReceivedAmount(BigDecimal.ZERO);
                    item.setPendingAmount(BigDecimal.ZERO);
                }
                filled.add(item);
            }
            return filled;
        } catch (Exception e) {
            log.error("查询副业收入按日统计失败：{}", e.getMessage(), e);
            throw new ServerException("查询副业收入按日统计失败", e);
        }
    }

    private BigDecimal sumAmount(LocalDate start, LocalDate end, String status) {
        LambdaQueryWrapper<SideIncomeRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), SideIncomeRecordEntity::getStatus, status);
        wrapper.ge(start != null, SideIncomeRecordEntity::getRecordDate, start);
        wrapper.le(end != null, SideIncomeRecordEntity::getRecordDate, end);
        List<SideIncomeRecordEntity> list = sideIncomeRecordMapper.selectList(wrapper);
        return list.stream()
                .map(SideIncomeRecordEntity::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateSourceAndStatus(String sourceType, String status) {
        if (sourceType != null && !SOURCE_TYPES.contains(sourceType)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "来源类型不合法，仅支持 self/partner/other");
        }
        if (status != null && !STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "状态不合法，仅支持 paid/pending");
        }
    }

    /**
     * 自销：未填流水时默认等于应得；合作：建议单独填流水，未填则不强行推断。
     */
    private BigDecimal resolveGrossAmount(String sourceType, BigDecimal amount, BigDecimal grossAmount) {
        if (grossAmount != null) {
            return grossAmount;
        }
        if ("self".equals(sourceType) || "other".equals(sourceType)) {
            return amount;
        }
        return null;
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private SideIncomeRecordVo convertToVo(SideIncomeRecordEntity entity) {
        if (entity == null) {
            return null;
        }
        SideIncomeRecordVo vo = new SideIncomeRecordVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
