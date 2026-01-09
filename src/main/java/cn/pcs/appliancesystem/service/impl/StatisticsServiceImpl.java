package cn.pcs.appliancesystem.service.impl;

import cn.pcs.appliancesystem.entity.StatisticsVO;
import cn.pcs.appliancesystem.entity.StockIn;
import cn.pcs.appliancesystem.entity.StockOut;
import cn.pcs.appliancesystem.entity.Sale;
import cn.pcs.appliancesystem.mapper.SaleMapper;
import cn.pcs.appliancesystem.mapper.StockInMapper;
import cn.pcs.appliancesystem.mapper.StockOutMapper;
import cn.pcs.appliancesystem.mapper.ProductMapper;
import cn.pcs.appliancesystem.service.StatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final StockInMapper stockInMapper;
    private final StockOutMapper stockOutMapper;
    private final SaleMapper saleMapper;
    private final ProductMapper productMapper;
    
    @Override
    public List<StatisticsVO> getStockInStatistics(LocalDate startDate, LocalDate endDate) {
        // 查询入库记录及关联的产品信息
        LambdaQueryWrapper<cn.pcs.appliancesystem.entity.StockIn> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(cn.pcs.appliancesystem.entity.StockIn::getInTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(cn.pcs.appliancesystem.entity.StockIn::getInTime, endDate.atTime(23, 59, 59));
        }
        
        List<cn.pcs.appliancesystem.entity.StockIn> list = stockInMapper.selectList(wrapper);
        
        // 获取所有涉及的产品ID
        List<Long> productIds = list.stream()
                .map(cn.pcs.appliancesystem.entity.StockIn::getProductId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品信息
        Map<Long, String> productNamesMap;
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        } else {
            productNamesMap = new HashMap<>();
        }

        // 按日期分组统计
        Map<String, List<cn.pcs.appliancesystem.entity.StockIn>> grouped = list.stream()
                .collect(Collectors.groupingBy(item -> 
                    item.getInTime() != null ? item.getInTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知"));
        
        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, List<cn.pcs.appliancesystem.entity.StockIn>> entry : grouped.entrySet()) {
            int totalCount = entry.getValue().stream()
                    .mapToInt(cn.pcs.appliancesystem.entity.StockIn::getQuantity)
                    .sum();
            
            // 计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (cn.pcs.appliancesystem.entity.StockIn stockIn : entry.getValue()) {
                String productName = productNamesMap.get(stockIn.getProductId());
                if (productName != null) {
                    // 假设入库记录中包含单价，这里暂时设置为0
                    totalAmount = totalAmount.add(BigDecimal.ZERO);
                }
            }
            
            String firstProductName = entry.getValue().stream()
                .findFirst()
                .map(item -> productNamesMap.getOrDefault(item.getProductId(), "未知产品"))
                .orElse("未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(entry.getKey())
                    .count(totalCount)
                    .totalAmount(totalAmount)
                    .productName(firstProductName)
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
        
        // 获取所有涉及的产品ID
        List<Long> productIds = list.stream()
                .map(cn.pcs.appliancesystem.entity.StockOut::getProductId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品信息
        Map<Long, String> productNamesMap;
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        } else {
            productNamesMap = new HashMap<>();
        }

        Map<String, List<cn.pcs.appliancesystem.entity.StockOut>> grouped = list.stream()
                .collect(Collectors.groupingBy(item -> 
                    item.getOutTime() != null ? item.getOutTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知"));
        
        List<StatisticsVO> result = new ArrayList<>();
        for (Map.Entry<String, List<cn.pcs.appliancesystem.entity.StockOut>> entry : grouped.entrySet()) {
            int totalCount = entry.getValue().stream()
                    .mapToInt(cn.pcs.appliancesystem.entity.StockOut::getQuantity)
                    .sum();
            
            String firstProductName = entry.getValue().stream()
                .findFirst()
                .map(item -> productNamesMap.getOrDefault(item.getProductId(), "未知产品"))
                .orElse("未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(entry.getKey())
                    .count(totalCount)
                    .totalAmount(BigDecimal.ZERO)
                    .productName(firstProductName)
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
        
        // 获取所有涉及的产品ID
        List<Long> productIds = list.stream()
                .map(cn.pcs.appliancesystem.entity.Sale::getProductId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品信息
        Map<Long, String> productNamesMap;
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        } else {
            productNamesMap = new HashMap<>();
        }

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
            
            String firstProductName = entry.getValue().stream()
                .findFirst()
                .map(item -> productNamesMap.getOrDefault(item.getProductId(), "未知产品"))
                .orElse("未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(entry.getKey())
                    .count(totalCount)
                    .totalAmount(totalAmount)
                    .productName(firstProductName)
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
        
        // 获取所有涉及的产品ID
        List<Long> productIds = list.stream()
                .map(cn.pcs.appliancesystem.entity.Sale::getProductId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询产品信息
        Map<Long, String> productNamesMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        }
        
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
            
            String productName = productNamesMap.getOrDefault(entry.getKey(), "未知产品");
            result.add(StatisticsVO.builder()
                    .label(productName)
                    .count(totalCount)
                    .totalAmount(totalAmount)
                    .productName(productName)
                    .build());
        }
        
        return result;
    }
}

