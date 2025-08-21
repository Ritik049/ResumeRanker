package com.resume.matcher.util;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.io.File;

public class PdfGenerator {

    private static final int MAX_LINE_LENGTH = 90;
    private static final int START_Y = 700;
    private static final int LINE_SPACING = 16;
    private static final int PAGE_MARGIN = 50;
    private static final int MIN_Y = 50; // Prevent text overflow

    // Method to generate a multi-page PDF
    public static void generatePDF(String formattedText, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.setLeading(LINE_SPACING);
            contentStream.newLineAtOffset(PAGE_MARGIN, START_Y);

            int yPosition = START_Y;

            // Handling word wrapping & pagination
            String[] paragraphs = formattedText.split("\n");
            for (String paragraph : paragraphs) {
                yPosition = wrapText(contentStream, paragraph, document, yPosition);
            }

            contentStream.endText();
            contentStream.close();

            document.save(new File(filePath));
            System.out.println("PDF saved successfully at " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles word wrapping and adds new pages when needed
    private static int wrapText(PDPageContentStream contentStream, String text, PDDocument document, int yPosition) throws IOException {
        String[] words = text.split(" ");
        StringBuilder wrappedLine = new StringBuilder();

        for (String word : words) {
            if (wrappedLine.length() + word.length() > MAX_LINE_LENGTH) {
                if (yPosition <= MIN_Y) { // Add a new page when reaching the bottom
                    contentStream.endText();
                    contentStream.close();

                    PDPage newPage = new PDPage();
                    document.addPage(newPage);
                    contentStream = new PDPageContentStream(document, newPage);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.setLeading(LINE_SPACING);
                    contentStream.newLineAtOffset(PAGE_MARGIN, START_Y);
                    yPosition = START_Y;
                }

                contentStream.showText(wrappedLine.toString());
                contentStream.newLine();
                yPosition -= LINE_SPACING;
                wrappedLine = new StringBuilder();
            }
            wrappedLine.append(word).append(" ");
        }

        if (!wrappedLine.isEmpty()) {
            if (yPosition <= MIN_Y) { // Add another page if needed
                contentStream.endText();
                contentStream.close();

                PDPage newPage = new PDPage();
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.setLeading(LINE_SPACING);
                contentStream.newLineAtOffset(PAGE_MARGIN, START_Y);
                yPosition = START_Y;
            }

            contentStream.showText(wrappedLine.toString());
            contentStream.newLine();
            yPosition -= LINE_SPACING;
        }

        return yPosition;
    }


}
