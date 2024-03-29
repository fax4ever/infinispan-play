package fax.play;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SaleFactory {

   private final Random random = new Random(739);

   public long randomDay(LocalDate startDay, int max) {
      return epochMilli(startDay.plusDays(random.nextInt(max)));
   }

   public HashMap<String, Sale> chunk(LocalDate day, int start, int end) {
      HashMap<String, Sale> bulk = new HashMap<>();
      for (int i = start; i < end; i++) {
         Sale sale = create(day, i);
         bulk.put(sale.getId(), sale);
      }
      return bulk;
   }

   private Sale create(LocalDate day, int ordinal) {
      String id = day.format(DateTimeFormatter.ISO_DATE) + ":" + String.format("%09d", ordinal);
      Code code = Code.values()[random.nextInt(Code.values().length)];
      Status status = Status.values()[random.nextInt(Status.values().length)];
      long moment = epochMilli(day);

      return new Sale(id, code.name(), status.name(), moment);
   }

   private static long epochMilli(LocalDate day) {
      return day.atTime(0, 0).toInstant(ZoneOffset.UTC).toEpochMilli();
   }

   public static Long countAllValues(Map<String, Long> aggregationResult) {
      return aggregationResult.values().stream().reduce((o, o2) -> o + o2).get();
   }

   public static Map<String, Long> convert(List<Object[]> aggregationResult) {
      return aggregationResult.stream().collect(Collectors.toMap(
            objects -> (String) objects[0], objects -> (long) objects[1]));
   }
}
