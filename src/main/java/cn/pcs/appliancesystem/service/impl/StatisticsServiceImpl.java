package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.StatisticsVO;
import cn.pcs.appliancesystem.mapper.SaleMapper;
import cn.pcs.appliancesystem.mapper.StockInMapper;
import cn.pcs.appliancesystem.mapper.StockOutMapper;
import cn.pcs.appliancesystem.service.StatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final StockInMapper stockInMapper;
    private final StockOutMapper stockOutMapper;
    private final SaleMapper saleMapper;
    
    @Override
    public List<StatisticsVO> getStockInStatistics(LocalDate startDate, LocalDate endDate) {
        // 查询入库记录
        LambdaQueryWrapper<cn.pcs.appliancesystem.entity.StockIn> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(cn.pcs.appliancesystem.entity.StockIn::getInTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(cn.pcs.appliancesystem.entity.StockIn::getInTime, endDate.atTime(23, 59, 59));
        }
        
        List<cn.pcs.appliancesystem.entity.StockIn> list = stockInMapper.selectList(wrapper);
        
        // 按日期分组统计
        Map<String, List<cn.pcs.appliancesystem.entity.StockIn>> grouped = list.stream()
                .collect(Collectors.groupingBy(item -> 
                    item.getInTime() != null ? item.getInTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知"));
        
        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, List<cn.pcs.appliancesystem.entity.StockIn>> entry : grouped.entrySet()) {
            int totalCount = entry.getValue().stream()
                    .mapToInt(cn.pcs.appliancesystem.entity.StockIn::getQuantity)
                    .sum();
            result.add(StatisticsVO.builder()
                    .label(entry.getKey())
                    .count(totalCount)
                    .totalAmount(BigDecimal.ZERO)
                    .build());
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO> getStockOutStatistics(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<cn.pcs.appliancesystem.entity.StockOut> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(cn.pcs.appliancesystem.entity.StockOut::getOutTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(cn.pcs.appliancesystem.entity.StockOut::getOutTime, endDate.atTime(23, 59, 59));
        }
        
        List<cn.pcs.appliancesystem.entity.StockOut> list = stockOutMapper.selectList(wrapper);
        
        Map<String, List<cn.pcs.appliancesystem.entity.StockOut>> grouped = list.stream()
                .collect(Collectors.groupingBy(item -> 
                    item.getOutTime() != null ? item.getOutTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知"));
        
        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, List<cn.pcs.appliancesystem.entity.StockOut>> entry : grouped.entrySet()) {
            int totalCount = entry.getValue().stream()
                    .mapToInt(cn.pcs.appliancesystem.entity.StockOut::getQuantity)
                    .sum();
            result.add(StatisticsVO.builder()
                    .label(entry.getKey())
                    .count(totalCount)
                    .totalAmount(BigDecimal.ZERO)
                    .build());
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO> getSaleStatistics(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<cn.pcs.appliancesystem.entity.Sale> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(cn.pcs.appliancesystem.entity.Sale::getSaleTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(cn.pcs.appliancesystem.entity.Sale::getSaleTime, endDate.atTime(23, 59, 59));
        }
        
        List<cn.pcs.appliancesystem.entity.Sale> list = saleMapper.selectList(wrapper);
        
        Map<String, List<cn.pcs.appliancesystem.entity.Sale>> grouped = list.stream()
                .collect(Collectors.groupingBy(item -> 
                    item.getSaleTime() != null ? item.getSaleTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知"));
        
        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, List<cn.pcs.appliancesystem.entity.Sale>> entry : grouped.entrySet()) {
            int totalCount = entry.getValue().stream()
                    .mapToInt(cn.pcs.appliancesystem.entity.Sale::getQuantity)
                    .sum();
            BigDecimal totalAmount = entry.getValue().stream()
                    .map(cn.pcs.appliancesystem.entity.Sale::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(StatisticsVO.builder()
                    .label(entry.getKey())
                    .count(totalCount)
                    .totalAmount(totalAmount)
                    .build());
        }
        
        return result;
    }
    
    @Override
    public List<StatisticsVO> getSaleStatisticsByProduct(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<cn.pcs.appliancesystem.entity.Sale> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(cn.pcs.appliancesystem.entity.Sale::getSaleTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(cn.pcs.appliancesystem.entity.Sale::getSaleTime, endDate.atTime(23, 59, 59));
        }
        
        List<cn.pcs.appliancesystem.entity.Sale> list = saleMapper.selectList(wrapper);
        
        // 需要关联产品表获取产品名称，这里简化处理，使用产品ID
        Map<Long, List<cn.pcs.appliancesystem.entity.Sale>> grouped = list.stream()
                .collect(Collectors.groupingBy(cn.pcs.appliancesystem.entity.Sale::getProductId));
        
        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<Long, List<cn.pcs.appliancesystem.entity.Sale>> entry : grouped.entrySet()) {
            int totalCount = entry.getValue().stream()
                    .mapToInt(cn.pcs.appliancesystem.entity.Sale::getQuantity)
                    .sum();
            BigDecimal totalAmount = entry.getValue().stream()
                    .map(cn.pcs.appliancesystem.entity.Sale::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.add(StatisticsVO.builder()
                    .label("产品ID: " + entry.getKey())
                    .count(totalCount)
                    .totalAmount(totalAmount)
                    .build());
        }
        
        return result;
    }
}

