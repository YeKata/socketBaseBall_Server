package base.dao;

import java.sql.SQLException;

import base.vo.Member;


public class MemberDAOImpl extends DAO implements MemberDAO{

	@Override
	public boolean checkId(String id) {
		try {
			String sql = "SELECT * FROM member WHERE mid = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(rs != null)rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {}
		}
		return true;
	}

	@Override
	public boolean joinMember(Member vo) {
		
		String sql = "INSERT INTO member("
				+ "mid, mpw,mname) "
				+ "VALUES(?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getmId());
			pstmt.setString(2, vo.getmPw());
			pstmt.setString(3, vo.getmName());
			if(pstmt.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}finally {
			try {
				if(pstmt!=null)pstmt.close();
			} catch (SQLException e) {}
		}
		return false;
	}

	@Override
	public Member loginMember(Member vo) {
		
		String sql = "SELECT * FROM member "
				   + " WHERE mid = ? AND mpw = ? ";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getmId());
			pstmt.setString(2, vo.getmPw());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String name = rs.getString("mname");
				vo.setmName(name);
				vo.setSuccess(true);
			}
		} catch (SQLException e) {
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {}
		}
		return vo;
	}
	public Member selectMember(Member vo) {
		Member mem = null;
		String sql = "Select * from member where mid=? and mpw = ?" ;
		try {
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, vo.getmId());
		pstmt.setString(2, vo.getmPw());
		rs = pstmt.executeQuery();
		if(rs.next()) {
			mem = new Member(rs.getString("mid"),rs.getString("mpw"),rs.getString("mname"));
		} 
			
	} catch (SQLException e) {
		e.printStackTrace();
	}
		return mem;
	}

	
}









