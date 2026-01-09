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
        Map<Long, String> productNamesMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        }
        
        // 返回每条单独的入库记录，而不是按日期聚合
        List<StatisticsVO> result = new ArrayList<>();
        for (cn.pcs.appliancesystem.entity.StockIn stockIn : list) {
            String productName = productNamesMap.getOrDefault(stockIn.getProductId(), "未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(stockIn.getInTime() != null ? stockIn.getInTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知日期")
                    .count(stockIn.getQuantity())
                    .totalAmount(BigDecimal.ZERO) // 入库记录中没有单价，总金额设为0
                    .productName(productName)
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
        Map<Long, String> productNamesMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        }
        
        // 返回每条单独的出库记录，而不是按日期聚合
        List<StatisticsVO> result = new ArrayList<>();
        for (cn.pcs.appliancesystem.entity.StockOut stockOut : list) {
            String productName = productNamesMap.getOrDefault(stockOut.getProductId(), "未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(stockOut.getOutTime() != null ? stockOut.getOutTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知日期")
                    .count(stockOut.getQuantity())
                    .totalAmount(BigDecimal.ZERO) // 出库记录中没有单价，总金额设为0
                    .productName(productName)
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
        Map<Long, String> productNamesMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<cn.pcs.appliancesystem.entity.Product> products = productMapper.selectBatchIds(productIds);
            productNamesMap = products.stream()
                    .collect(Collectors.toMap(cn.pcs.appliancesystem.entity.Product::getId, 
                            cn.pcs.appliancesystem.entity.Product::getProductName));
        }
        
        // 返回每条单独的销售记录，包含产品名称、时间、销售数量和金额
        List<StatisticsVO> result = new ArrayList<>();
        for (cn.pcs.appliancesystem.entity.Sale sale : list) {
            String productName = productNamesMap.getOrDefault(sale.getProductId(), "未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(sale.getSaleTime() != null ? sale.getSaleTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知日期")
                    .count(sale.getQuantity())
                    .totalAmount(sale.getTotalPrice())
                    .productName(productName)
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
        
        // 返回每条单独的销售记录，包含产品名称、时间、销售数量和金额
        List<StatisticsVO> result = new ArrayList<>();
        for (cn.pcs.appliancesystem.entity.Sale sale : list) {
            String productName = productNamesMap.getOrDefault(sale.getProductId(), "未知产品");
            
            result.add(StatisticsVO.builder()
                    .label(sale.getSaleTime() != null ? sale.getSaleTime().toLocalDate().format(DateTimeFormatter.ISO_DATE) : "未知日期")
                    .count(sale.getQuantity())
                    .totalAmount(sale.getTotalPrice())
                    .productName(productName)
                    .build());
        }
        
        return result;
    }
}

