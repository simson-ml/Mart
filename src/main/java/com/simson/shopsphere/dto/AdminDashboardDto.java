package com.simson.shopsphere.dto;

import com.simson.shopsphere.entity.Order;
import com.simson.shopsphere.entity.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardDto {
    private BigDecimal totalSales = BigDecimal.ZERO;
    private BigDecimal salesToday = BigDecimal.ZERO;
    private long totalOrders;
    private long totalUsers;
    private long totalProducts;
    private long lowStockCount;
    private long pendingOrdersCount;
    private long deliveredOrdersCount;
    private long pendingReturnsCount;
    private List<Order> recentOrders = new ArrayList<>();
    private List<Product> lowStockProducts = new ArrayList<>();
    private Map<String, Long> categoryDistribution = new LinkedHashMap<>();
    private Map<String, BigDecimal> salesByMonth = new LinkedHashMap<>();

    public AdminDashboardDto() {}

    public AdminDashboardDto(BigDecimal totalSales, BigDecimal salesToday, long totalOrders, long totalUsers, long totalProducts,
                             long lowStockCount, long pendingOrdersCount, long deliveredOrdersCount, long pendingReturnsCount,
                             List<Order> recentOrders, List<Product> lowStockProducts,
                             Map<String, Long> categoryDistribution, Map<String, BigDecimal> salesByMonth) {
        this.totalSales = totalSales;
        this.salesToday = salesToday;
        this.totalOrders = totalOrders;
        this.totalUsers = totalUsers;
        this.totalProducts = totalProducts;
        this.lowStockCount = lowStockCount;
        this.pendingOrdersCount = pendingOrdersCount;
        this.deliveredOrdersCount = deliveredOrdersCount;
        this.pendingReturnsCount = pendingReturnsCount;
        this.recentOrders = recentOrders;
        this.lowStockProducts = lowStockProducts;
        this.categoryDistribution = categoryDistribution;
        this.salesByMonth = salesByMonth;
    }

    public static AdminDashboardDtoBuilder builder() {
        return new AdminDashboardDtoBuilder();
    }

    public static class AdminDashboardDtoBuilder {
        private BigDecimal totalSales = BigDecimal.ZERO;
        private BigDecimal salesToday = BigDecimal.ZERO;
        private long totalOrders;
        private long totalUsers;
        private long totalProducts;
        private long lowStockCount;
        private long pendingOrdersCount;
        private long deliveredOrdersCount;
        private long pendingReturnsCount;
        private List<Order> recentOrders = new ArrayList<>();
        private List<Product> lowStockProducts = new ArrayList<>();
        private Map<String, Long> categoryDistribution = new LinkedHashMap<>();
        private Map<String, BigDecimal> salesByMonth = new LinkedHashMap<>();

        public AdminDashboardDtoBuilder totalSales(BigDecimal totalSales) { this.totalSales = totalSales; return this; }
        public AdminDashboardDtoBuilder salesToday(BigDecimal salesToday) { this.salesToday = salesToday; return this; }
        public AdminDashboardDtoBuilder totalOrders(long totalOrders) { this.totalOrders = totalOrders; return this; }
        public AdminDashboardDtoBuilder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public AdminDashboardDtoBuilder totalProducts(long totalProducts) { this.totalProducts = totalProducts; return this; }
        public AdminDashboardDtoBuilder lowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; return this; }
        public AdminDashboardDtoBuilder pendingOrdersCount(long pendingOrdersCount) { this.pendingOrdersCount = pendingOrdersCount; return this; }
        public AdminDashboardDtoBuilder deliveredOrdersCount(long deliveredOrdersCount) { this.deliveredOrdersCount = deliveredOrdersCount; return this; }
        public AdminDashboardDtoBuilder pendingReturnsCount(long pendingReturnsCount) { this.pendingReturnsCount = pendingReturnsCount; return this; }
        public AdminDashboardDtoBuilder recentOrders(List<Order> recentOrders) { this.recentOrders = recentOrders; return this; }
        public AdminDashboardDtoBuilder lowStockProducts(List<Product> lowStockProducts) { this.lowStockProducts = lowStockProducts; return this; }
        public AdminDashboardDtoBuilder categoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; return this; }
        public AdminDashboardDtoBuilder salesByMonth(Map<String, BigDecimal> salesByMonth) { this.salesByMonth = salesByMonth; return this; }

        public AdminDashboardDto build() {
            return new AdminDashboardDto(totalSales, salesToday, totalOrders, totalUsers, totalProducts,
                    lowStockCount, pendingOrdersCount, deliveredOrdersCount, pendingReturnsCount,
                    recentOrders, lowStockProducts, categoryDistribution, salesByMonth);
        }
    }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public BigDecimal getSalesToday() { return salesToday; }
    public void setSalesToday(BigDecimal salesToday) { this.salesToday = salesToday; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getPendingOrdersCount() { return pendingOrdersCount; }
    public void setPendingOrdersCount(long pendingOrdersCount) { this.pendingOrdersCount = pendingOrdersCount; }

    public long getDeliveredOrdersCount() { return deliveredOrdersCount; }
    public void setDeliveredOrdersCount(long deliveredOrdersCount) { this.deliveredOrdersCount = deliveredOrdersCount; }

    public long getPendingReturnsCount() { return pendingReturnsCount; }
    public void setPendingReturnsCount(long pendingReturnsCount) { this.pendingReturnsCount = pendingReturnsCount; }

    public List<Order> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<Order> recentOrders) { this.recentOrders = recentOrders; }

    public List<Product> getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(List<Product> lowStockProducts) { this.lowStockProducts = lowStockProducts; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public Map<String, BigDecimal> getSalesByMonth() { return salesByMonth; }
    public void setSalesByMonth(Map<String, BigDecimal> salesByMonth) { this.salesByMonth = salesByMonth; }
}
