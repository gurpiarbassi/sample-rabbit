package uk.gov.hmrc.ma.sample.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.AccessType;

@Entity
@Table(name = "my_table")
@NoArgsConstructor
@Getter
@AccessType(AccessType.Type.FIELD)
@ToString
@SuppressWarnings("PMD.ImmutableField")

public class MyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;


}
