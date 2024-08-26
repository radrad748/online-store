package rad.online.store.utils;

import rad.online.store.entities.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageNumbersUtil {

    public static List<Integer> getPageNumber(int page, long countProducts, int move, int anchor, List<? extends Object> list) {
        List<Integer> numbers = new ArrayList<>();
        if (list.isEmpty())
            return numbers;

        if (page == 0 || page == 1 || page == 2) {
            numbersFirstOfThreePages(countProducts, page, numbers);
            return numbers;
        }

        if (page > 3)
            numbersOverThreePages(countProducts, move, anchor, numbers);

        return numbers;
    }

    private static void numbersFirstOfThreePages(long countProducts, int page, List<Integer> numbers) {
        if (countProducts <= 12)
            numbers.add(1);

        if (countProducts > 12 && countProducts <= 24)
            Collections.addAll(numbers, 1, 2);


        if (countProducts > 24)
            Collections.addAll(numbers, 1, 2, 3);
    }

    private static void numbersOverThreePages(long countProducts, int move, int anchor, List<Integer> numbers) {
        int totalPages = (int) Math.ceil((double) countProducts / 12);

        switch (move) {
            case 0:
                moveNumbers(totalPages, anchor, numbers);
                break;
            case 1:
                if (anchor + 3 > totalPages)
                    moveNumbers(totalPages, anchor, numbers);
                else
                    moveNumbers(totalPages, anchor + 3, numbers);
                break;
            case 2:
                moveNumbers(totalPages, anchor - 3, numbers);
                break;
        }
    }

    private static void moveNumbers(int totalPages, int anchor, List<Integer> numbers) {
        int pages = anchor;
        for (int i = 0; i < 3; i++) {
            if (pages > totalPages)
                break;
            numbers.add(pages);
            pages++;
        }
    }

}
