package base.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import base.vo.Member;
import base.vo.Rank;

public class Client {
	
	Socket client;
	// 로그인 완료된 회원 정보
	Member member;

	public Client(Socket client) {
		this.client = client;
		serverClientReceive();
	}
	
	// client에서 출력되는 정보를 읽어들임
	public void serverClientReceive() {
		MainController.threadPool.submit(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream ois = null;
				try {
					Object obj;
					while(true) {
						ois = new ObjectInputStream(
							client.getInputStream()
						);
						if((obj = ois.readObject()) != null) {
							System.out.println("요청");
							if(obj instanceof Member) {
								System.out.println("회원관련 요청");
								desposeMember((Member)obj);
							}else if(obj instanceof Rank) {
								System.out.println("게임기록 갱신 요청");
								requestGameInfo((Rank)obj);
							}
						}
						
					}
				} catch (Exception e) {
					removeClient();
				}
			}
		});
	}
	
	public void requestGameInfo(Rank vo) {
		System.out.println("request game info "+ vo);
		// 기존 기록 존재 여부 확인
		Rank old = MainController.rankDAO.getRanking(vo.getMid(),vo.getCount());
		System.out.println("기존 기록 "+old);
		System.out.println("신규 기록 "+vo);
		if(old == null) {
			// 신규 기록
			MainController.rankDAO.insertRank(vo);
		}else if(vo.getCount() < old.getCount()){
			// 기록 갱신
			MainController.rankDAO.updateRank(vo);
		} else if (vo.getCount() < old.getCount() && vo.getRegDate() < old.getRegDate()) {
			MainController.rankDAO.updateDate(vo);
		}
		// 신규 삽입 또는 갱신 기록 전송
		vo = MainController.rankDAO.getRanking( vo.getMid(),vo.getCount());
		System.out.println("sendData = "+vo);
		MainController.rankDAO.insertRank(vo);
		sendData(vo);
	}

	// 회원관련 요청 처리
	public void desposeMember(Member obj) {
		System.out.println("receive Member "+obj);
		switch(obj.getOrder()) {
		case 0 :
			// 아이디 중복 체크
			String memberId = obj.getmId();
			boolean isCheck = MainController.memberDAO.checkId(memberId);
			obj.setSuccess(isCheck);
			break;
		case 1 : 
			// 회원가입
			MainController.mc.appendText("회원가입 요청 - "+obj.getmName());
			isCheck = MainController.memberDAO.joinMember(obj);
			obj.setSuccess(isCheck);
			break;
		case 2 : 
			// 로그인
			Member vo = MainController.memberDAO.loginMember(obj);
			if(vo.isSuccess()) {
				// 회원 정보 등록
				member = obj = vo;
				System.out.println("LOGIN - " + member);
			}
			break;
		}
		System.out.println("send Member "+obj);
		sendData(obj);
	}

	// 연결되어 있는 client에 정보를 출력
	public synchronized void sendData(Object data) {
		ObjectOutputStream oos = null;
		try {
			OutputStream os = client.getOutputStream();
			oos = new ObjectOutputStream(os);
			oos.writeObject(data);
			oos.flush();
		} catch (IOException e) {
			removeClient();
		}
	}
	
	// client 연결 종료
	public void removeClient() {
		String ip = client.getInetAddress().getHostAddress();
		MainController.mc.appendText(new Date()+ip+"연결 종료");
		System.out.println("Client 연결 종료 : " + ip);
		
		synchronized (MainController.clients) {
			MainController.clients.remove(this);
		}
		
		if(client != null && !client.isClosed()) {
			try {
				client.close();
			} catch (IOException e) {}
		}
	}
}










