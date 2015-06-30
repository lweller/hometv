package ch.wellernet.hometv.master.impl.dao.hibernate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.wellernet.hometv.master.api.model.Renderer;
import ch.wellernet.hometv.master.impl.dao.RendererDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@SqlGroup({ @Sql(scripts = "inittestdata.sql", executionPhase = BEFORE_TEST_METHOD),
        @Sql(scripts = "cleanup.sql", executionPhase = AFTER_TEST_METHOD) })
public class RendererJpaDaoImplTest {
    @Configuration()
    static class TestConfiguration extends AbstractPersistentTestConfiguration<Integer, Renderer, RendererDao> {
        /**
         * @see ch.wellernet.hometv.master.impl.dao.hibernate.AbstractPersistentTestConfiguration#dao()
         */
        @Override
        public RendererDao dao() {
            return new RendererJpaDaoImpl();
        }
    }

    // under test
    @Resource
    private RendererDao rendererDao;

    @Before
    public void setup() {
    }

    @Test
    public void shouldFindNoRenderer() {
        // given
        // nothing special

        // when
        Renderer renderer = rendererDao.findByInfo("devxy");

        // then
        assertThat(renderer, is(nullValue()));
    }

    @Test
    public void shouldFindOneRenderer() {
        // given
        // nothing special

        // when
        Renderer renderer = rendererDao.findByInfo("dev01");

        // then
        assertThat(renderer, is(not(nullValue())));
        assertThat(renderer.getId(), is(20));
    }
}
