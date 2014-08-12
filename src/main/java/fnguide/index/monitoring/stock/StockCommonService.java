package fnguide.index.monitoring.stock;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Service;

@Service
public class StockCommonService extends SqlSessionDaoSupport{
	public String GetUnmByUcd(String u_cd){
		String u_nm = (String) getSqlSession().selectOne("StockConstitutionQueryMapper.selectUnmWithUcd", u_cd);		
		return u_nm;
	}
}
