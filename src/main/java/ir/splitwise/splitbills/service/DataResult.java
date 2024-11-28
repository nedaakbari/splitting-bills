package ir.splitwise.splitbills.service;

public interface DataResult {
     DataJsonResult executeJson();

    DataExcelResult executeExcel();
}
