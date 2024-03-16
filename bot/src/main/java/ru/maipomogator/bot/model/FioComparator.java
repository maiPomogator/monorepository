package ru.maipomogator.bot.model;

import java.util.Comparator;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FioComparator implements Comparator<Professor> {
    private final String[] input;

    public FioComparator(String input) {
        this.input = input.toLowerCase().split(" ");
        if (this.input.length == 1) {
            log.info("Searching by F");
        } else if (this.input.length == 2) {
            log.info("Searching by IO");
        } else {
            log.info("Searching by all");
        }
    }

    @Override
    public int compare(Professor pr1, Professor pr2) {
        if (input.length == 1) {
            return compareByF(pr1, pr2);
        } else if (input.length == 2) {
            return compareByIO(pr1, pr2);
        } else {

            String[] pr1FioParts = pr1.getFullName().toLowerCase().split(" ");
            String[] pr2FioParts = pr2.getFullName().toLowerCase().split(" ");

            int dist1 = 0;
            int dist2 = 0;

            for (int i = 0; i < Math.min(pr1FioParts.length, input.length); i++) {
                dist1 += wagnerFischer(input[i], pr1FioParts[i]);
            }
            for (int i = 0; i < Math.min(pr2FioParts.length, input.length); i++) {
                dist2 += wagnerFischer(input[i], pr2FioParts[i]);
            }
            return Integer.compare(dist1, dist2);
        }
    }

    private int compareByF(Professor pr1, Professor pr2) {
        String pr1F = pr1.lastName().toLowerCase();
        String pr2F = pr2.lastName().toLowerCase();

        int dist1 = wagnerFischer(input[0], pr1F);
        int dist2 = wagnerFischer(input[0], pr2F);

        return Integer.compare(dist1, dist2);
    }

    private int compareByIO(Professor pr1, Professor pr2) {
        String pr1I = pr1.firstName().toLowerCase();
        String pr2I = pr2.firstName().toLowerCase();
        String pr1O = pr1.middleName().toLowerCase();
        String pr2O = pr2.middleName().toLowerCase();

        int dist1 = 0;
        int dist2 = 0;

        dist1 += wagnerFischer(input[0], pr1I);
        dist1 += wagnerFischer(input[1], pr1O);

        dist2 += wagnerFischer(input[0], pr2I);
        dist2 += wagnerFischer(input[1], pr2O);

        return Integer.compare(dist1, dist2);
    }

    private int wagnerFischer(String str1, String str2) {
        int str1Length = str1.length();
        int str2Length = str2.length();
        if (str1Length > str2Length) {
            String temp = str1;
            str1 = str2;
            str2 = temp;
            int tempLen = str1Length;
            str1Length = str2Length;
            str2Length = tempLen;
        }

        int[] currentRow = new int[str1Length + 1];
        for (int i = 0; i <= str1Length; i++) {
            currentRow[i] = i;
        }

        for (int i = 1; i <= str2Length; i++) {
            int[] previousRow = currentRow.clone();
            currentRow[0] = i;
            for (int j = 1; j <= str1Length; j++) {
                int add = previousRow[j] + 1;
                int delete = currentRow[j - 1] + 1;
                int change = previousRow[j - 1] + (str1.charAt(j - 1) != str2.charAt(i - 1) ? 1 : 0);
                currentRow[j] = Math.min(Math.min(add, delete), change);
            }
        }

        return currentRow[str1Length];
    }
}