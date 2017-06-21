package TransactionJob;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ExpectedException extends SQLException
{
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	ExpectedException(int reason, String source)
	{
		log.error("There was an error in the orderline with the ID " + reason + " in the " + source);
	}
}
