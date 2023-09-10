package com.gsm.blabla.global;

import com.google.common.base.CaseFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class DatabaseCleanup implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
            .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
            .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, e.getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE \"" + tableName + "\"").executeUpdate();
            // TODO: entity PK id 통일하고 코드 수정하기
            if (tableName.equals("VOICE_FILE") || tableName.equals("CREW_REPORT")) {
                entityManager.createNativeQuery("ALTER TABLE \"" + tableName + "\" ALTER COLUMN \"" + tableName + "_ID\" RESTART WITH 1").executeUpdate();
            }
            else if (tableName.equals("GOOGLE_ACCOUNT") || tableName.equals("APPLE_ACCOUNT")) {
                continue;
            }
            else {
                entityManager.createNativeQuery("ALTER TABLE \"" + tableName + "\" ALTER COLUMN ID RESTART WITH 1").executeUpdate();
            }
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
