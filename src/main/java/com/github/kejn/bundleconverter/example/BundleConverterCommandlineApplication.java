package com.github.kejn.bundleconverter.example;

import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.github.kejn.bundleconverter.BundleGroup;
import com.github.kejn.bundleconverter.Bundles;
import com.github.kejn.bundleconverter.converter.XlsxConverter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Simple command-line application showing some possible features of the
 * "bundle-converter" library.
 * 
 * @author kejn
 */
public class BundleConverterCommandlineApplication {

    private static void printHelp() {
	out.println("Usage:");
        out.println("Convert properties to xlsx:");
        out.println("\tjava -jar bundle-converter-cmd.jar -xlsx pathToDirectoryWithBundles [outputFilePath]");
        out.println("Convert xlsx to properties:");
        out.println("\tjava -jar bundle-converter-cmd.jar -properties pathToXlsxFile outputDirPath [templatePropertiesFile]");
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidFormatException {
	out.println("Running app with arguments:\n" + Arrays.asList(args));

	if (args.length < 1) {
	    printHelp();
	    return;
	}

        switch (args[0]) {
        case "-xlsx":
            properties2xlsx(args);
            break;
        case "-properties":
            xlsx2properties(args);
            break;
        default:
            printHelp();
        }

    }

    private static void properties2xlsx(String[] args) throws IOException, FileNotFoundException {
        if (args.length < 2) {
            printHelp();
            return;
        }
        File dir = new File(args[1]);
        List<BundleGroup> groups = Bundles.groupsInDirectory(dir);

        XlsxConverter converter = new XlsxConverter();
        Workbook wb = converter.toXlsx(groups);

        String filename = dir.getPath() + File.separator;
        if (args.length > 2) {
            filename = args[2];
        } else {
            out.print("Using default output filename...");
            filename += "bundles.xlsx";
        }
        wb.write(new FileOutputStream(filename));
        wb.close();
        out.println("Conversion finished.");
    }

    private static void xlsx2properties(String[] args) throws InvalidFormatException, IOException {
        if (args.length < 3) {
            printHelp();
            return;
        }

        File templateFile = null;
        if (args.length >= 4) {
            templateFile = new File(args[3]);
        }

        File xlsxFile = new File(args[1]);
        Workbook wb = new XSSFWorkbook(xlsxFile);
        
        File outputDirectory = new File(args[2]);

        XlsxConverter converter = new XlsxConverter();
        List<BundleGroup> groups = converter.toBundleGroupList(wb, outputDirectory);

        for (BundleGroup group : groups) {
            group.saveGroupAsPropertiesFiles(templateFile);
        }
        wb.close();
        out.println("Conversion finished.");
    }

}
