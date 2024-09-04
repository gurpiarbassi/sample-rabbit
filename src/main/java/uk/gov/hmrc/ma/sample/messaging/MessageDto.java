package uk.gov.hmrc.ma.sample.messaging;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Value
@Jacksonized
public class MessageDto {
  String someData;
}
