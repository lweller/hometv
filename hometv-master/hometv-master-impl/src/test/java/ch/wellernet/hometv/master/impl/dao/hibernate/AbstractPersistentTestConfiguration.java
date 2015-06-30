/**
 *
 */
package ch.wellernet.hometv.master.impl.dao.hibernate;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import ch.wellernet.hibernate.dao.GenericDao;
import ch.wellernet.hibernate.model.IdentifyableObject;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author Lucien Weller <lucien@wellernet.ch>
 */
public abstract class AbstractPersistentTestConfiguration<ID, T extends IdentifyableObject<ID>, DAO extends GenericDao<ID, T>> {
    @Bean
    public abstract DAO dao();

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:hometv;DB_CLOSE_DELAY=-1;LOCK_MODE=0;REFERENTIAL_INTEGRITY=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS HOMETV");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        dataSource.setMinPoolSize(3);
        dataSource.setMaxPoolSize(10);
        dataSource.setMaxIdleTime(300);
        dataSource.setPreferredTestQuery("SELECT 1");
        dataSource.setTestConnectionOnCheckout(true);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean EntityManagerFactory() throws PropertyVetoException {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("ch.wellernet.hometv.util.usertypes", "ch.wellernet.hometv.master.api.model",
                "ch.wellernet.hometv.master.impl.model");
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", H2Dialect.class.getName());
        entityManagerFactoryBean.setJpaProperties(properties);
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
