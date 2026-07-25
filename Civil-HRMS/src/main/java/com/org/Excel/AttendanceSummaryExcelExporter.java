package com.org.Excel;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.org.Entity.Timesheet;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttendanceSummaryExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Timesheet> summaryList;
    private String companyName;
    private String monthStr;
    private int daysInMonth;

    public AttendanceSummaryExcelExporter(List<Timesheet> summaryList, String companyName, String monthStr) {
        this.summaryList = summaryList;
        this.companyName = companyName;
        this.monthStr = monthStr;
        this.workbook = new XSSFWorkbook();
        try {
            YearMonth parsedMonth = YearMonth.parse(monthStr); // Expects "YYYY-MM"
            this.daysInMonth = parsedMonth.lengthOfMonth();
        } catch (Exception e) {
            this.daysInMonth = 30;
        }
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Attendance Summary");
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(companyName + " - Attendance Summary (" + monthStr + ")");
        
        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        Row headerRow = sheet.createRow(2);
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = { 
            "EMPLOYEE NAME", "COMPANY NAME", "TOTAL DAYS", "PRESENT DAYS", 
            "ABSENT DAYS", "HALF DAY", "FULL NIGHT", "HALF NIGHT", "SUNDAY","HOLYDAYS", "TOTAL ATTENDANCE DAYS" 
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeDataLines() {
        int rowCount = 3;
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font);

        for (Timesheet record : summaryList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            // 1. EMPLOYEE NAME
            String fullName = "";
            if (record.getUser() != null) {
                String firstName = record.getUser().getFirstName() != null ? record.getUser().getFirstName() : "";
                String lastName = record.getUser().getLastName() != null ? record.getUser().getLastName() : "";
                fullName = (firstName + " " + lastName).trim();
            }
            createCell(row, columnCount++, fullName, cellStyle);

            // 2. COMPANY NAME
            String compName = (record.getUser() != null && record.getUser().getCompany() != null) 
                ? record.getUser().getCompany().getName() : "";
            createCell(row, columnCount++, compName, cellStyle);

            // 3. TOTAL DAYS
            createCell(row, columnCount++, this.daysInMonth, cellStyle);

            // Get raw statuses
            String rawStatus = record.getStatus();  
            String nightStatus = record.getNightStatus();

            LocalDate today = LocalDate.now();
            
            YearMonth reportMonth = YearMonth.parse( monthStr);
            
            int sundayCount = 0;

            if (reportMonth.equals(YearMonth.now())) {

                int lastDay = today.getDayOfMonth();

                for (int day = 1; day <= lastDay; day++) {
                    if (reportMonth.atDay(day).getDayOfWeek() == DayOfWeek.SUNDAY) {
                        sundayCount++;
                    }
                }

            } else {

                for (int day = 1; day <= reportMonth.lengthOfMonth(); day++) {
                    if (reportMonth.atDay(day).getDayOfWeek() == DayOfWeek.SUNDAY) {
                        sundayCount++;
                    }
                }

            }
            // Calculate metrics using upgraded smart parser engine
            double presentDays = countStatus(rawStatus, "Present");
            double absentDays  = countStatus(rawStatus, "Absent");
            double halfDays    = countStatus(rawStatus, "Half Day");
            double holiDays    = countStatus(rawStatus, "holi Day");
            double sundayDays = sundayCount;
            double fullNights  = countStatus(nightStatus, "Full Night");
            double halfNights  = countStatus(nightStatus, "Half Night");

            // Total Attendance formula calculation
            double totalAttendance = presentDays + (halfDays * 0.5) + fullNights + (halfNights * 0.5)+sundayDays+ holiDays;

            // Populate data fields
            createCell(row, columnCount++, presentDays, cellStyle);     // 4. PRESENT DAYS
            createCell(row, columnCount++, absentDays, cellStyle);       // 5. ABSENT DAYS
            createCell(row, columnCount++, halfDays, cellStyle);         // 6. HALF DAY
            createCell(row, columnCount++, fullNights, cellStyle);       // 7. FULL NIGHT
            createCell(row, columnCount++, halfNights, cellStyle);       // 8. HALF NIGHT
            createCell(row, columnCount++, sundayDays, cellStyle);       // 9. SUNDAY
            createCell(row, columnCount++, holiDays, cellStyle); 
            createCell(row, columnCount++, totalAttendance, cellStyle); // 10. TOTAL ATTENDANCE DAYS
        }

        // Auto-size columns (0 to 9)
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private double countStatus(String rawStatus, String target) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) {
            return 0.0;
        }
        String cleanStatus = rawStatus.trim();

        // 1. Handle explicit mass summary keywords
        if (cleanStatus.equalsIgnoreCase("All Present") || cleanStatus.equalsIgnoreCase("Present All")) {
            return target.equalsIgnoreCase("Present") ? this.daysInMonth : 0.0;
        }
        if (cleanStatus.equalsIgnoreCase("All Leave") || cleanStatus.equalsIgnoreCase("All Absent") || cleanStatus.equalsIgnoreCase("Absent All")) {
            return target.equalsIgnoreCase("Absent") ? this.daysInMonth : 0.0;
        }

        // 2. Upgraded Regex: Extract totals even when mixed like "22 Present, 4 Absent"
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*" + Pattern.quote(target), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(cleanStatus);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        // 3. Fallback: Day-by-day mapping logic via comma arrays (e.g., "Present, Present, Absent")
        double count = 0;
        String[] days = cleanStatus.split(",");
        for (String dayStatus : days) {
            if (dayStatus.trim().equalsIgnoreCase(target)) {
                count++;
            }
        }
        return count;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
