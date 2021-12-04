package base.dao;

import base.vo.Rank;

public interface RankDAO {
	
	public int insertRank(Rank vo);
	
	public int updateRank(Rank vo);

	public Rank getRanking(String mid,int count);
	
	public int updateDate(Rank vo);
}
