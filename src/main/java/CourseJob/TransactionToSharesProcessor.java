package CourseJob;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;

class TransactionToSharesProcessor implements ItemProcessor<TransactionShareRateDTO, SharesDTO>
{
	@Override
	public SharesDTO process(TransactionShareRateDTO tsrDTO) throws Exception
	{
		SharesDTO sharesDTO = new SharesDTO();
		sharesDTO.setAmount(CalculateShares(tsrDTO));
		sharesDTO.setAccountNumber(tsrDTO.getAccountNumber());
		sharesDTO.setFundID(tsrDTO.getFundID());
		sharesDTO.setThrowsException(getException(tsrDTO));
		return sharesDTO;
	}

	private BigDecimal CalculateShares(TransactionShareRateDTO tsrDTO)
	{
		return tsrDTO.getSum().divide(tsrDTO.getShareRate());
	}

	private boolean getException(TransactionShareRateDTO tsrDTO)
	{
		return tsrDTO.isTransactionException() || tsrDTO.isSharesException();
	}
}
