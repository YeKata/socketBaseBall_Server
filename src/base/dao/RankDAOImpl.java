package base.dao;

import java.sql.SQLException;

import base.vo.Rank;

public class RankDAOImpl extends DAO implements RankDAO {

	String sql;
	
	@Override
	public int insertRank(Rank vo) {
		int result = 0;
		sql = "INSERT INTO base_rank"
				+" VALUES(?,?,?)"+
				" ON DUPLICATE KEY UPDATE"
				+" count=?, regdate=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getMid());
			pstmt.setInt(2, vo.getCount());
			pstmt.setLong(3, vo.getRegDate());
			pstmt.setInt(4, vo.getCount());
			pstmt.setLong(5, vo.getRegDate());
			result= pstmt.executeUpdate();
		} catch (SQLException e) {
		e.printStackTrace();
		}finally {
			try {
				if(pstmt!=null)pstmt.close();
			} catch (SQLException e) {}
		}
		return result;
	}

	@Override
	public int updateRank(Rank vo) {
		sql = "UPDATE base_Rank "
				 + "SET count = ? , RegDate = ? "
				 + " WHERE mid= ?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1,vo.getCount());
				pstmt.setLong(2, vo.getRegDate());
				pstmt.setString(3, vo.getMid());
				return pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				try {
					pstmt.close();
				} catch (SQLException e) {}
			}
			return 0;
	}

	@Override
	public Rank getRanking(String mid, int count) {
		Rank vo = null;
		sql ="SELECT * FROM base_rank WHERE mid = ? and count = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mid);
			pstmt.setInt(2, count);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				System.out.println(rs.toString());
				vo = new Rank(
					rs.getString("mid"),
					rs.getInt("count"),
					rs.getInt("regDate")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vo;
		
	}

	@Override
	public int updateDate(Rank vo) {
		sql = "UPDATE base_Rank "
				 + "SET RegDate = ? "
				 + " WHERE mid= ?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(1, vo.getRegDate());
				pstmt.setString(2, vo.getMid());
				return pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				try {
					pstmt.close();
				} catch (SQLException e) {}
			}
			return 0;
	}

	
	
	

}
