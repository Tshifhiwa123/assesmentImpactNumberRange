import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;


public interface NumberRangeSummarizer {

    //collect the input
    Collection<Integer> collect(String input);

    //get the summarized string
    String summarizeCollection(Collection<Integer> input);


    public class RangeSummarizerTest {

        //Attribute
        private RangeSummarizer summaryRange;

        /*
         * Void method states when tests start
         */
        @BeforeAll
        public void setupMessage(){
            System.out.println("Starting Test cases");
        }

        /*
         * Creates an RangeSummarizer Object before each test case.
         */
        @BeforeEach
        public void setUp() {
            this.summaryRange = new RangeSummarizer();
        }

        /**
         * Tests Collect Method
         */
        @ParameterizedTest(name = "{index} => value inserted is {0}")
        @ValueSource(strings ={
                "", "1", "1,3", "1,2,3,4", "0,2,3,4,6,8,9", "1,3,6,7,8,12,13,14,15,21,22,23,24,31",
                "1,2,4,6,8,9,10,12,13,14", "2,4,6,8,10", "1,2,3,4,5,6,7,8,9,10"
        })
        public void collect(String values) {
            Collection<Integer> actual = summaryRange.collect(values);

            //Tests when input is empty
            if (values.equals(""))
                assertEquals(Arrays.asList(), actual);
            else {
                //Collects the expected result
                Collection<Integer> expected = Arrays.asList(values.split(","))
                        .stream()
                        .map(Integer::parseInt)
                        .collect(toList());

                assertIterableEquals(expected, actual);
            }
        }

        /**
         * Tests summarizeCollection Method
         */
        @ParameterizedTest(name = "{index} => Input: {0} | Output: {1}")
        @MethodSource("inputsToSummarizeCollection")
        public void summarizeCollection(String input, String expected) {
            assertEquals(expected, summaryRange.summarizeCollection(summaryRange.collect(input)));
        }

        /**
         * Method uses a stream of arguments to collect input and expected output strings.
         * Returns a Stream of Arguments
         */
        private static Stream<Arguments> inputsToSummarizeCollection(){
            return Stream.of(
                    Arguments.of("", "Empty List"),
                    Arguments.of("1","1"),
                    Arguments.of("1,3", "1, 3"),
                    Arguments.of("1,2,3,4", "1-4"),
                    Arguments.of("0,2,3,4,6,8,9", "0, 2-4, 6, 8-9"),
                    Arguments.of("1,3,6,7,8,12,13,14,15,21,22,23,24,31", "1, 3, 6-8, 12-15, 21-24, 31"),
                    Arguments.of("1,2,4,6,8,9,10,12,13,14", "1-2, 4, 6, 8-10, 12-14"),
                    Arguments.of("2,4,6,8,10", "2, 4, 6, 8, 10"),
                    Arguments.of("1,2,3,4,5,6,7,8,9,10", "1-10")
            );
        }

        /*
         * Void method states when all tests have executed
         */
        @AfterAll
        public void exitMessage(){
            System.out.println("Ending Test Cases");
        }

    }


    public class RangeSummarizer implements NumberRangeSummarizer{


        //Attributes
        private StringBuilder stringValue;
        private AtomicInteger start;

        /**
         * Constructor creates a StringBuilder and AtomicInteger object
         */
        public RangeSummarizer(){
            this.stringValue = new StringBuilder();
            this.start = new AtomicInteger(0);
        }

        /**
         * Method collects the input
         *
         * @param input accepts strings
         * @return Collection of Integer values
         */
        public Collection<Integer> collect(String input){
            //returns empty list if an empty string is found
            if(input.equals(""))
                return Arrays.asList();

            //Splits the string by commas, maps values to type Integer and collects resultant data in Collection<Integer>.
            return Arrays.asList(input.replaceAll("\\s", "")
                            .split(","))
                    .stream()
                    .map(Integer::parseInt)
                    .collect(toList());
        }

        /**
         * Method gets the result summarized string
         *
         * @param input Collection<Integer>
         * @return summarized string or "Empty List" if the input is empty
         */
        public String summarizeCollection(Collection<Integer> input){
            //Converts Collection to List
            List<Integer> seq = input.stream().collect(toCollection((ArrayList::new)));

            //Checks if list is empty
            if (input.isEmpty())
                return "Empty List";

        /**Retrieving indexes of starting ranges via filter
         and implemnt correct ranges to stringBuilder via for Each loop.**/
            IntStream.range(1, input.size())
                    .filter(i ->  seq.get(i-1) + 1 != seq.get(i))
                    .forEach(i -> {
                        if (i - 1 == start.get())
                            stringValue.append(String.valueOf(seq.get(start.get()))).append(", ");
                        else
                            stringValue.append(seq.get(start.get()) + "-" + seq.get(i-1)).append(", ");
                        start.set(i);
                    });

            /**
            *Implementing final ranges to the stringBuilder Object.*
             * */
            if (seq.size() - 1 == start.get())
                stringValue.append(String.valueOf(seq.get(start.get())));
            else
                stringValue.append(seq.get(start.get()) + "-" + seq.get(seq.size() - 1));

            return stringValue.toString();
        }

    }

    /**
     * My Main Method
     */
    public class Main {
        public static void main(String[] args) {
            RangeSummarizer summaryRange = new RangeSummarizer();
            Collection<Integer> array = summaryRange.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
            System.out.println(summaryRange.summarizeCollection(array));
        }
    }
}



