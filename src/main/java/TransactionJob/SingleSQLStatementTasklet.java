package TransactionJob;

import javax.sql.DataSource;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

class SingleSQLStatementTasklet implements Tasklet
{
	private DataSource dataSource;

	private String sql;
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	public void setSql(String sql)
	{
		this.sql = sql;
	}

	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception
	{
		new JdbcTemplate(this.dataSource).execute(this.sql);
		return RepeatStatus.FINISHED;

	}
}
