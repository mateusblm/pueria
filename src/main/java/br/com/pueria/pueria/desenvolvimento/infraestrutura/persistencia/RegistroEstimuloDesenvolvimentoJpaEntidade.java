package br.com.pueria.pueria.desenvolvimento.infraestrutura.persistencia;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity @Table(name="registros_estimulos_desenvolvimento") public class RegistroEstimuloDesenvolvimentoJpaEntidade {
 @Id private UUID id; @Column(name="crianca_id",nullable=false) private UUID criancaId; @Column(name="estimulo_id",nullable=false) private UUID estimuloId; @Column(length=500) private String observacao; @Column(name="experimentado_em",nullable=false) private LocalDateTime experimentadoEm;
 protected RegistroEstimuloDesenvolvimentoJpaEntidade(){} public RegistroEstimuloDesenvolvimentoJpaEntidade(UUID id,UUID criancaId,UUID estimuloId,String observacao,LocalDateTime experimentadoEm){this.id=id;this.criancaId=criancaId;this.estimuloId=estimuloId;this.observacao=observacao;this.experimentadoEm=experimentadoEm;}
 public UUID getId(){return id;} public UUID getCriancaId(){return criancaId;} public UUID getEstimuloId(){return estimuloId;} public String getObservacao(){return observacao;} public LocalDateTime getExperimentadoEm(){return experimentadoEm;}
}
