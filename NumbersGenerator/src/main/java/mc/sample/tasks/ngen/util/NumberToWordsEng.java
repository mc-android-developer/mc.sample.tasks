package mc.sample.tasks.ngen.util;

import java.text.DecimalFormat;

public class NumberToWordsEng {
    private static final long MAX_CONVERTIBLE_NUMBER = 999999999999L;

    private static final String[] TENS_NAMES = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] NUM_NAMES = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = NUM_NAMES[number % 100];
            number /= 100;
        } else {
            soFar = NUM_NAMES[number % 10];
            number /= 10;

            soFar = TENS_NAMES[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return NUM_NAMES[number] + " hundred" + soFar;
    }


    public static String convert(long number) {
        if (number < 0 || number > MAX_CONVERTIBLE_NUMBER) {
            throw new IllegalArgumentException("Number must be between 0 and " + MAX_CONVERTIBLE_NUMBER);
        }

        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }

        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        String str = df.format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(str.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(str.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(str.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(str.substring(9, 12));

        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions)
                        + " billion ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions)
                        + " billion ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = convertLessThanOneThousand(millions)
                        + " million ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions)
                        + " million ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = "one thousand ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands)
                        + " thousand ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    // Test
    public static void main(String[] args) {
        System.out.println("*** " + NumberToWordsEng.convert(0));
        System.out.println("*** " + NumberToWordsEng.convert(1));
        System.out.println("*** " + NumberToWordsEng.convert(16));
        System.out.println("*** " + NumberToWordsEng.convert(100));
        System.out.println("*** " + NumberToWordsEng.convert(118));
        System.out.println("*** " + NumberToWordsEng.convert(200));
        System.out.println("*** " + NumberToWordsEng.convert(219));
        System.out.println("*** " + NumberToWordsEng.convert(800));
        System.out.println("*** " + NumberToWordsEng.convert(801));
        System.out.println("*** " + NumberToWordsEng.convert(1316));
        System.out.println("*** " + NumberToWordsEng.convert(1000000));
        System.out.println("*** " + NumberToWordsEng.convert(2000000));
        System.out.println("*** " + NumberToWordsEng.convert(3000200));
        System.out.println("*** " + NumberToWordsEng.convert(700000));
        System.out.println("*** " + NumberToWordsEng.convert(9000000));
        System.out.println("*** " + NumberToWordsEng.convert(9001000));
        System.out.println("*** " + NumberToWordsEng.convert(123456789));
        System.out.println("*** " + NumberToWordsEng.convert(2147483647));
        System.out.println("*** " + NumberToWordsEng.convert(3000000010L));
    }
}