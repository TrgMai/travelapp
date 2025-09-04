package com.example.travelapp.ui.panels;

import com.example.travelapp.config.DataSourceProvider;
import com.example.travelapp.ui.theme.ThemeComponents;
import com.example.travelapp.ui.theme.ThemeTokens;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Collections;
import java.util.Locale;
import java.util.Calendar;

public class DashboardPanel extends JPanel {
    private static final int REFRESH_MS = 5000;

    private final JLabel bookingsKpi = new JLabel();
    private final JLabel revenueKpi = new JLabel();
    private final JLabel cancelRateKpi = new JLabel();

    private final JPanel chartsPanel = new JPanel(new GridLayout(2, 2, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16));

    private final CategoryChart tourBarChart;
    private final XChartPanel<CategoryChart> tourBarPanel;

    private final XYChart monthlyLineChart;
    private final XChartPanel<XYChart> monthlyLinePanel;

    private final PieChart typePieChart;
    private final XChartPanel<PieChart> typePiePanel;

    private final XYChart dailyAreaChart;
    private final XChartPanel<XYChart> dailyAreaPanel;

    private javax.swing.Timer refreshTimer;
    private final DecimalFormat moneyFmt;

    private final JSpinner startDate = new JSpinner(
            new SpinnerDateModel(java.sql.Date.valueOf(YearMonth.now().atDay(1)), null, null, Calendar.DAY_OF_MONTH));
    private final JSpinner endDate = new JSpinner(new SpinnerDateModel(
            java.sql.Date.valueOf(YearMonth.now().atEndOfMonth()), null, null, Calendar.DAY_OF_MONTH));
    private final JCheckBox ckCash = new JCheckBox("Tiền mặt", true);
    private final JCheckBox ckTransfer = new JCheckBox("Chuyển khoản", true);
    private final JCheckBox ckCard = new JCheckBox("Thẻ", true);

    private volatile boolean loading = false;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeTokens.SURFACE());
        setBorder(new EmptyBorder(ThemeTokens.SPACE_16, ThemeTokens.SPACE_16, ThemeTokens.SPACE_16,
                ThemeTokens.SPACE_16));

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setGroupingSeparator(',');
        dfs.setDecimalSeparator('.');
        moneyFmt = new DecimalFormat("#,###", dfs);

        JPanel filters = ThemeComponents.cardPanel();
        filters.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, ThemeTokens.SPACE_8, ThemeTokens.SPACE_12);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;

        startDate.setEditor(new JSpinner.DateEditor(startDate, "dd/MM/yyyy"));
        ((JSpinner.DateEditor) startDate.getEditor()).getTextField().setColumns(8);
        endDate.setEditor(new JSpinner.DateEditor(endDate, "dd/MM/yyyy"));
        ((JSpinner.DateEditor) endDate.getEditor()).getTextField().setColumns(8);

        addL(filters, gc, new JLabel("Khoảng ngày"));
        JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeTokens.SPACE_6, 0));
        range.setOpaque(false);
        range.add(startDate);
        range.add(new JLabel("đến"));
        range.add(endDate);
        filters.add(range, pos(gc));

        GridBagConstraints stretchR = pos(gc);
        stretchR.weightx = 1;
        stretchR.fill = GridBagConstraints.HORIZONTAL;
        filters.add(Box.createHorizontalStrut(1), stretchR);

        newRow(gc);

        addL(filters, gc, new JLabel("Thanh toán"));
        JPanel types = new JPanel(new FlowLayout(FlowLayout.LEFT, ThemeTokens.SPACE_8, 0));
        types.setOpaque(false);
        types.add(ckCash);
        types.add(ckTransfer);
        types.add(ckCard);
        filters.add(types, pos(gc));

        GridBagConstraints stretch = pos(gc);
        stretch.weightx = 1;
        stretch.fill = GridBagConstraints.HORIZONTAL;
        filters.add(Box.createHorizontalStrut(1), stretch);

        JButton applyBtn = ThemeComponents.primaryButton("Áp dụng");
        filters.add(applyBtn, pos(gc));

        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, ThemeTokens.SPACE_16, 0));
        kpiPanel.setOpaque(true);
        kpiPanel.setBackground(ThemeTokens.SURFACE());
        kpiPanel.add(createKpiCard("Số đặt chỗ", bookingsKpi));
        kpiPanel.add(createKpiCard("Doanh thu", revenueKpi));
        kpiPanel.add(createKpiCard("Tỷ lệ hủy", cancelRateKpi));

        JPanel header = new JPanel(new BorderLayout(0, ThemeTokens.SPACE_16));
        header.setOpaque(false);
        header.add(kpiPanel, BorderLayout.NORTH);
        header.add(filters, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        chartsPanel.setOpaque(true);
        chartsPanel.setBackground(ThemeTokens.SURFACE());
        add(chartsPanel, BorderLayout.CENTER);

        tourBarChart = new CategoryChartBuilder().title("Doanh thu theo tour").xAxisTitle("Tour")
                .yAxisTitle("Doanh thu").build();
        styleCategoryChart(tourBarChart);
        tourBarChart.getStyler().setYAxisDecimalPattern("#,###");
        tourBarChart.getStyler().setToolTipsEnabled(true);
        tourBarPanel = new XChartPanel<>(tourBarChart);

        monthlyLineChart = new XYChartBuilder().title("Doanh thu 12 tháng gần nhất").xAxisTitle("Thời gian")
                .yAxisTitle("Doanh thu").build();
        styleXYChartLine(monthlyLineChart);
        monthlyLineChart.getStyler().setYAxisDecimalPattern("#,###");
        monthlyLineChart.getStyler().setToolTipsEnabled(true);
        monthlyLinePanel = new XChartPanel<>(monthlyLineChart);

        typePieChart = new PieChartBuilder().title("Cơ cấu doanh thu theo thanh toán").build();
        stylePie(typePieChart);
        typePieChart.getStyler().setToolTipsEnabled(true);
        typePiePanel = new XChartPanel<>(typePieChart);

        dailyAreaChart = new XYChartBuilder().title("Doanh thu theo ngày").xAxisTitle("Ngày").yAxisTitle("Doanh thu")
                .build();
        styleXYChartArea(dailyAreaChart);
        dailyAreaChart.getStyler().setYAxisDecimalPattern("#,###");
        dailyAreaChart.getStyler().setToolTipsEnabled(true);
        dailyAreaPanel = new XChartPanel<>(dailyAreaChart);
        
        chartsPanel.add(tourBarPanel);
        chartsPanel.add(monthlyLinePanel);
        chartsPanel.add(typePiePanel);
        chartsPanel.add(dailyAreaPanel);
        
        enableMagnifyFrame(tourBarPanel, "Doanh thu theo tour", 4, 3, 12);
        enableMagnifyFrame(monthlyLinePanel, "Doanh thu 12 tháng", 4, 3, 12);
        enableMagnifyFrame(typePiePanel, "Cơ cấu doanh thu", 4, 4, 12);
        enableMagnifyFrame(dailyAreaPanel, "Doanh thu theo ngày", 4, 3, 12);

        initEmptySeries();

        applyBtn.addActionListener(e -> refreshAsync());
        Runnable onTypeChange = this::refreshAsync;
        ckCash.addActionListener(e -> onTypeChange.run());
        ckTransfer.addActionListener(e -> onTypeChange.run());
        ckCard.addActionListener(e -> onTypeChange.run());

        refreshAsync();
        refreshTimer = new javax.swing.Timer(REFRESH_MS, e -> refreshAsync());
        refreshTimer.setInitialDelay(REFRESH_MS);
        refreshTimer.start();
    }

    private void initEmptySeries() {
        updateCategorySafe(tourBarChart, "Doanh thu", java.util.Collections.emptyList(),
                java.util.Collections.emptyList());
        updateXYSafe(monthlyLineChart, "Doanh thu", java.util.Collections.emptyList(),
                java.util.Collections.emptyList());
        updatePieSafe(typePieChart, java.util.Collections.emptyMap());
        updateXYSafe(dailyAreaChart, "Theo ngày", java.util.Collections.emptyList(), java.util.Collections.emptyList());
    }

    private GridBagConstraints pos(GridBagConstraints gc) {
        gc.gridx++;
        return (GridBagConstraints) gc.clone();
    }

    private void addL(JPanel p, GridBagConstraints gc, JComponent l) {
        l.setForeground(ThemeTokens.MUTED());
        p.add(l, pos(gc));
    }

    private void newRow(GridBagConstraints gc) {
        gc.gridx = 0;
        gc.gridy++;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (refreshTimer != null && !refreshTimer.isRunning())
            refreshTimer.start();
    }

    @Override
    public void removeNotify() {
        if (refreshTimer != null)
            refreshTimer.stop();
        super.removeNotify();
    }

    private JPanel createKpiCard(String title, JLabel valueLabel) {
        JPanel card = ThemeComponents.cardPanel();
        card.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(
                new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_REGULAR, ThemeTokens.FONT_SIZE_BASE));
        titleLabel.setForeground(ThemeTokens.MUTED());
        valueLabel.setFont(new Font(ThemeTokens.FONT_FAMILY, ThemeTokens.FONT_WEIGHT_BOLD, ThemeTokens.FONT_SIZE_XL));
        valueLabel.setForeground(ThemeTokens.PRIMARY());
        valueLabel.setBorder(new EmptyBorder(ThemeTokens.SPACE_12, 0, 0, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void styleCategoryChart(CategoryChart c) {
        c.getStyler().setLegendVisible(false);
        c.getStyler().setXAxisLabelRotation(45);
        c.getStyler().setChartBackgroundColor(ThemeTokens.SURFACE());
        c.getStyler().setPlotBackgroundColor(ThemeTokens.SURFACE());
        c.getStyler().setPlotGridLinesColor(ThemeTokens.BORDER());
        c.getStyler().setChartFontColor(ThemeTokens.TEXT());
        c.getStyler().setAxisTickLabelsColor(ThemeTokens.TEXT());
        c.getStyler().setSeriesColors(ThemeTokens.CHART_PALETTE());
    }

    private void styleXYChartLine(XYChart c) {
        c.getStyler().setLegendVisible(false);
        c.getStyler().setChartBackgroundColor(ThemeTokens.SURFACE());
        c.getStyler().setPlotBackgroundColor(ThemeTokens.SURFACE());
        c.getStyler().setPlotGridLinesColor(ThemeTokens.BORDER());
        c.getStyler().setChartFontColor(ThemeTokens.TEXT());
        c.getStyler().setAxisTickLabelsColor(ThemeTokens.TEXT());
        c.getStyler()
                .setSeriesColors(new Color[] { ThemeTokens.PRIMARY(), ThemeTokens.SUCCESS(), ThemeTokens.WARNING() });
        c.getStyler().setMarkerSize(5);
    }

    private void styleXYChartArea(XYChart c) {
        styleXYChartLine(c);
        c.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        c.getStyler().setMarkerSize(0);
        c.getStyler()
                .setSeriesColors(new Color[] { ThemeTokens.PRIMARY(), ThemeTokens.SUCCESS(), ThemeTokens.WARNING() });
    }

    private void stylePie(PieChart c) {
        c.getStyler().setChartBackgroundColor(ThemeTokens.SURFACE());
        c.getStyler().setPlotBackgroundColor(ThemeTokens.SURFACE());
        c.getStyler().setChartFontColor(ThemeTokens.TEXT());
        c.getStyler().setLegendVisible(true);
        c.getStyler().setDecimalPattern("#,###");
        c.getStyler().setSeriesColors(ThemeTokens.CHART_PALETTE());
        try {
            Class<?> annoCls = Class.forName("org.knowm.xchart.style.PieStyler$AnnotationType");
            Object labelAndPct = Enum.valueOf((Class) annoCls, "LabelAndPercentage");
            c.getStyler().getClass().getMethod("setAnnotationType", annoCls).invoke(c.getStyler(), labelAndPct);
            try {
                c.getStyler().getClass().getMethod("setDrawAllAnnotations", boolean.class).invoke(c.getStyler(), true);
            } catch (NoSuchMethodException ignore) {
            }
        } catch (Exception e) {
            try {
                c.getStyler().getClass().getMethod("setHasAnnotations", boolean.class).invoke(c.getStyler(), true);
            } catch (Exception ignore) {
            }
        }
        c.getStyler().setToolTipsEnabled(true);
    }

    private void refreshAsync() {
        if (loading)
            return;
        loading = true;
        LocalDate from = toLocal((java.util.Date) startDate.getValue());
        LocalDate to = toLocal((java.util.Date) endDate.getValue());
        Set<String> types = new LinkedHashSet<>();
        if (ckCash.isSelected())
            types.add("CASH");
        if (ckTransfer.isSelected())
            types.add("TRANSFER");
        if (ckCard.isSelected())
            types.add("CARD");

        new SwingWorker<Stats, Void>() {
            @Override
            protected Stats doInBackground() {
                return fetchStats(from, to, types);
            }

            @Override
            protected void done() {
                try {
                    Stats s = get();
                    bookingsKpi.setText(moneyFmt.format(s.totalBookings));
                    revenueKpi.setText(moneyFmt.format(Math.round(s.totalRevenue)));
                    double cancelRate = s.totalBookings > 0 ? (double) s.cancelled / s.totalBookings * 100.0 : 0.0;
                    cancelRateKpi.setText(String.format(Locale.US, "%.1f%%", cancelRate));

                    updateCategorySafe(tourBarChart, "Doanh thu", s.tourNames, s.tourRevenues);
                    tourBarPanel.revalidate();
                    tourBarPanel.repaint();

                    updateXYSafe(monthlyLineChart, "Doanh thu", s.monthDates, s.monthAmounts);
                    monthlyLinePanel.revalidate();
                    monthlyLinePanel.repaint();

                    updatePieSafe(typePieChart, s.typeAmounts);
                    typePiePanel.revalidate();
                    typePiePanel.repaint();

                    updateXYSafe(dailyAreaChart, "Theo ngày", s.dayDates, s.dayAmounts);
                    dailyAreaPanel.revalidate();
                    dailyAreaPanel.repaint();
                } catch (Exception ignored) {
                } finally {
                    loading = false;
                }
            }
        }.execute();
    }

    private LocalDate toLocal(java.util.Date d) {
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Stats fetchStats(LocalDate from, LocalDate to, Set<String> types) {
        Stats s = new Stats();
        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            String typeFilter = buildTypeFilter(types, "p.type");

            String sqlBk = "SELECT b.status, COUNT(*) FROM bookings b WHERE b.created_at BETWEEN ? AND ? GROUP BY b.status";
            try (PreparedStatement ps = conn.prepareStatement(sqlBk)) {
                ps.setDate(1, java.sql.Date.valueOf(from));
                ps.setDate(2, java.sql.Date.valueOf(to));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String st = rs.getString(1);
                        int count = rs.getInt(2);
                        s.totalBookings += count;
                        if ("CANCELED".equalsIgnoreCase(st))
                            s.cancelled = count;
                    }
                }
            }

            String sqlTotal = "SELECT COALESCE(SUM(p.amount),0) FROM payments p WHERE p.paid_at BETWEEN ? AND ? "
                    + typeFilter.replace("WHERE", "AND");
            try (PreparedStatement ps = conn.prepareStatement(sqlTotal)) {
                int idx = 1;
                ps.setDate(idx++, java.sql.Date.valueOf(from));
                ps.setDate(idx++, java.sql.Date.valueOf(to));
                idx = setTypeParams(ps, idx, types);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        s.totalRevenue = rs.getDouble(1);
                }
            }

            String sqlTour = "SELECT t.name, COALESCE(SUM(p.amount),0) FROM payments p "
                    + "JOIN bookings b ON b.id = p.booking_id "
                    + "JOIN tours t ON t.id = b.tour_id "
                    + "WHERE p.paid_at BETWEEN ? AND ? " + typeFilter.replace("WHERE", "AND")
                    + "GROUP BY t.name ORDER BY SUM(p.amount) DESC LIMIT 12";
            try (PreparedStatement ps = conn.prepareStatement(sqlTour)) {
                int idx = 1;
                ps.setDate(idx++, java.sql.Date.valueOf(from));
                ps.setDate(idx++, java.sql.Date.valueOf(to));
                idx = setTypeParams(ps, idx, types);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString(1);
                        s.tourNames.add((name == null || name.isBlank()) ? "N/A" : name);
                        s.tourRevenues.add(rs.getDouble(2));
                    }
                }
            }

            String sqlMonthly = "SELECT DATE_TRUNC('month', p.paid_at) m, COALESCE(SUM(p.amount),0) FROM payments p "
                    + "WHERE p.paid_at >= ? AND p.paid_at <= ? " + typeFilter.replace("WHERE", "AND")
                    + "GROUP BY m ORDER BY m";
            try (PreparedStatement ps = conn.prepareStatement(sqlMonthly)) {
                int idx = 1;
                ps.setDate(idx++, java.sql.Date.valueOf(from.minusMonths(11).withDayOfMonth(1)));
                ps.setDate(idx++, java.sql.Date.valueOf(to));
                idx = setTypeParams(ps, idx, types);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Timestamp ts = rs.getTimestamp(1);
                        s.monthDates.add(new java.util.Date(ts.getTime()));
                        s.monthAmounts.add(rs.getDouble(2));
                    }
                }
            }

            String sqlDaily = "SELECT CAST(p.paid_at AS DATE) d, COALESCE(SUM(p.amount),0) FROM payments p "
                    + "WHERE p.paid_at BETWEEN ? AND ? " + typeFilter.replace("WHERE", "AND")
                    + "GROUP BY d ORDER BY d";
            try (PreparedStatement ps = conn.prepareStatement(sqlDaily)) {
                int idx = 1;
                ps.setDate(idx++, java.sql.Date.valueOf(from));
                ps.setDate(idx++, java.sql.Date.valueOf(to));
                idx = setTypeParams(ps, idx, types);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Date d = rs.getDate(1);
                        s.dayDates.add(new java.util.Date(d.getTime()));
                        s.dayAmounts.add(rs.getDouble(2));
                    }
                }
            }

            String sqlPie = "SELECT CASE WHEN p.type IS NULL OR p.type NOT IN ('CASH','TRANSFER','CARD') THEN 'OTHER' ELSE p.type END t, "
                    + "COALESCE(SUM(p.amount),0) FROM payments p WHERE p.paid_at BETWEEN ? AND ? "
                    + typeFilter.replace("WHERE", "AND") + " GROUP BY t ORDER BY 2 DESC";
            try (PreparedStatement ps = conn.prepareStatement(sqlPie)) {
                int idx = 1;
                ps.setDate(idx++, java.sql.Date.valueOf(from));
                ps.setDate(idx++, java.sql.Date.valueOf(to));
                idx = setTypeParams(ps, idx, types);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String type = rs.getString(1);
                        double amt = rs.getDouble(2);
                        String label = switch (type) {
                            case "CASH" -> "Tiền mặt";
                            case "TRANSFER" -> "Chuyển khoản";
                            case "CARD" -> "Thẻ";
                            default -> "Khác";
                        };
                        s.typeAmounts.put(type, new LabeledAmount(label, amt));
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return s;
    }

    private String buildTypeFilter(Set<String> types, String col) {
        if (types.isEmpty())
            return "";
        return " WHERE " + col + " IN (" + String.join(",", Collections.nCopies(types.size(), "?")) + ") ";
    }

    private int setTypeParams(PreparedStatement ps, int startIdx, Set<String> types) throws SQLException {
        List<String> ordered = new ArrayList<>();
        if (types.contains("CASH"))
            ordered.add("CASH");
        if (types.contains("TRANSFER"))
            ordered.add("TRANSFER");
        if (types.contains("CARD"))
            ordered.add("CARD");
        int idx = startIdx;
        for (String t : ordered) {
            ps.setString(idx++, t);
        }
        return idx;
    }
    private static class Stats {
        int totalBookings = 0;
        double totalRevenue = 0d;
        int cancelled = 0;
        List<String> tourNames = new ArrayList<>();
        List<Number> tourRevenues = new ArrayList<>();
        List<java.util.Date> monthDates = new ArrayList<>();
        List<Number> monthAmounts = new ArrayList<>();
        List<java.util.Date> dayDates = new ArrayList<>();
        List<Number> dayAmounts = new ArrayList<>();
        Map<String, LabeledAmount> typeAmounts = new LinkedHashMap<>();
    }

    private static class LabeledAmount {
        final String label;
        final double amount;

        LabeledAmount(String label, double amount) {
            this.label = label;
            this.amount = amount;
        }
    }

    private static final String EMPTY_SERIES = "__empty__";
    private static final String PLACEHOLDER_LABEL = "—";

    private void updateCategorySafe(CategoryChart chart, String seriesName, List<String> x, List<? extends Number> y) {
        int n = Math.min(x == null ? 0 : x.size(), y == null ? 0 : y.size());
        if (n <= 0) {
            if (chart.getSeriesMap().containsKey(seriesName))
                chart.removeSeries(seriesName);
            if (!chart.getSeriesMap().containsKey(EMPTY_SERIES)) {
                chart.addSeries(EMPTY_SERIES, java.util.Collections.singletonList(PLACEHOLDER_LABEL),
                        java.util.Collections.singletonList(0));
            }
            return;
        }
        if (chart.getSeriesMap().containsKey(EMPTY_SERIES))
            chart.removeSeries(EMPTY_SERIES);
        java.util.List<String> xs = new java.util.ArrayList<>(n);
        java.util.List<Number> ys = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String label = x.get(i);
            if (label == null || label.trim().isEmpty())
                label = "N/A";
            xs.add(label);
            Number v = y.get(i);
            ys.add(v == null ? 0 : v.doubleValue());
        }
        if (chart.getSeriesMap().containsKey(seriesName))
            chart.updateCategorySeries(seriesName, xs, ys, null);
        else
            chart.addSeries(seriesName, xs, ys);
    }

    private void updateXYSafe(XYChart chart, String seriesName, List<java.util.Date> x, List<? extends Number> y) {
        int n = Math.min(x == null ? 0 : x.size(), y == null ? 0 : y.size());
        if (n <= 0) {
            if (chart.getSeriesMap().containsKey(seriesName))
                chart.removeSeries(seriesName);
            if (!chart.getSeriesMap().containsKey(EMPTY_SERIES)) {
                chart.addSeries(EMPTY_SERIES, java.util.Collections.singletonList(new java.util.Date()),
                        java.util.Collections.singletonList(0));
            }
            return;
        }
        if (chart.getSeriesMap().containsKey(EMPTY_SERIES))
            chart.removeSeries(EMPTY_SERIES);
        java.util.List<java.util.Date> xs = new java.util.ArrayList<>(n);
        java.util.List<Number> ys = new java.util.ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            xs.add(x.get(i));
            Number v = y.get(i);
            ys.add(v == null ? 0 : v);
        }
        if (chart.getSeriesMap().containsKey(seriesName))
            chart.updateXYSeries(seriesName, xs, ys, null);
        else {
            XYSeries s = chart.addSeries(seriesName, xs, ys);
            s.setMarker(SeriesMarkers.CIRCLE);
        }
    }

    private void updatePieSafe(PieChart chart, Map<String, LabeledAmount> data) {
        chart.getSeriesMap().clear();
        if (data == null || data.isEmpty()) {
            chart.addSeries("Không có dữ liệu", 1);
            return;
        }
        for (LabeledAmount la : data.values()) {
            chart.addSeries(la.label, Math.max(0, la.amount));
        }
    }

    private void enableMagnifyFrame(XChartPanel<?> src, String title, double wScale, double hScale, int padding) {
        src.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e))
                    showChartFrame(src, title, wScale, hScale, padding);
            }
        });
    }

    private void showChartFrame(XChartPanel<?> src, String title, double wScale, double hScale, int padding) {
        java.awt.Window owner = javax.swing.SwingUtilities.getWindowAncestor(src);
        javax.swing.JFrame f = new javax.swing.JFrame(title);
        f.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.JPanel wrapper = new javax.swing.JPanel(new java.awt.BorderLayout());
        wrapper.setBorder(new javax.swing.border.EmptyBorder(padding, padding, padding, padding));
        XChartPanel<?> big = new XChartPanel<>(src.getChart());
        wrapper.add(big, java.awt.BorderLayout.CENTER);
        f.setContentPane(wrapper);

        java.awt.Dimension cur = src.getSize();
        if (cur.width <= 0 || cur.height <= 0)
            cur = new java.awt.Dimension(800, 500);
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.min((int) Math.round(cur.width * wScale), (int) (scr.width * 0.9));
        int h = Math.min((int) Math.round(cur.height * hScale), (int) (scr.height * 0.9));

        f.setSize(w, h);
        f.setLocationRelativeTo(owner);
        f.setResizable(true);

        f.getRootPane().registerKeyboardAction(e -> f.dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        f.setVisible(true);
    }

}
