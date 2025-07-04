package user.restarea.detail.review;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.cipher.DataDecryption;
import user.account.login.LoginDTO;
import user.util.AreaDetailReviewRangeDTO;
import user.util.RangeDTO;

public class RestAreaDetailReviewService {

	public List<AreaDetailReviewDTO> searchAllReview(int area_num, AreaDetailReviewRangeDTO adrrDTO) {
		List<AreaDetailReviewDTO> list = null;
		
		RestAreaDetailReviewDAO radrDAO = new RestAreaDetailReviewDAO();
		
		try {
			list = radrDAO.seleteAllReview(area_num, adrrDTO);
			
			String myKey = "asdf1234asdf1234";
			DataDecryption dd = new DataDecryption(myKey);
			
			for(AreaDetailReviewDTO item : list) {
				try {
					item.setName(dd.decrypt(item.getName()));
				} catch (Exception e) {
					System.err.println("복호화 실패 사유 : " + e.getMessage() + " / 원본 이름 : " + item.getName());
				}// end try-catch
			}// end for
			
		} catch (SQLException e) {
			e.printStackTrace();
		}// end try-catch
		
		return list;
	}// searchAllReview
	
	public boolean addReview(int area_num, String msg, LoginDTO lDTO) {
		boolean flag = false;
		
		RestAreaDetailReviewDAO radrDAO = new RestAreaDetailReviewDAO();
		
		try {
			flag = radrDAO.insertReview(area_num, msg, lDTO);
		} catch (SQLException e) {
			e.printStackTrace();
		}// end try-catch
		
		return flag;
	}// addReview
	
	public boolean modifyReviewReport(int rev_num) {
		boolean flag = false;
		
		RestAreaDetailReviewDAO radrDAO = new RestAreaDetailReviewDAO();
		
		try {
			flag = radrDAO.updateReviewReport(rev_num);
		} catch (SQLException e) {
			e.printStackTrace();
		}// end try-catch
		
		return flag;
	}// modifyReviewReport
	
	/********************************* pagination ************************************/
	
	/**
	 * 1. 총 게시물(데이터)의 카운트 구하기.
	 * @param id 휴게소넘버
	 * @return
	 */
	public int searchTotalCount(int id) {
		int cnt = 0;
		
		RestAreaDetailReviewDAO radrDAO = new RestAreaDetailReviewDAO();

		try {
			cnt = radrDAO.selectTotalCount(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}// end try-catch
		
		return cnt;
	}// searchTotalCount
	
	/**
	 * 2. 페이지에 보여질 게시물의 수
	 * @param scale
	 * @return
	 */
	public int pageScale(int scale) {
		int pageScale = 0;
		
		pageScale = scale;
		
		return pageScale;
	}// pageScale
	
	/**
	 * 3. 총 페이지를 구합니다.
	 * @param totalCount 총 게시물(레코드)의 수
	 * @param pageScale  한 화면에 보여질 게시물의 수
	 * @return 총 페이지 수
	 */
	public int totalPage(int totalCount, int pageScale) {
		int totalPage = 0;

		totalPage = (int) Math.ceil((double) totalCount / pageScale);

		return totalPage;
	}// totalPage
	
	/**
	 * 4. 현재 페이지(currentPage)에서 게시물의 시작 번호를 구합니다.
	 * @param pageScale 보여질 페이지 수
	 * @param rDTO
	 * @return
	 */
	public int startNum(int pageScale, AreaDetailReviewRangeDTO adrrDTO) {
		int startNum = 0;

		startNum = (adrrDTO.getCurrentPage() * pageScale) - pageScale + 1;
		adrrDTO.setStartNum(startNum);

		return startNum;
	}// startNum
	
	
	/**
	 * 5. 현재 페이지(currentPage)에서 게시물의 끝 번호를 구합니다.
	 * @param pageScale 보여질 페이지 수
	 * @param rDTO
	 * @return
	 */
	public int endNum(int pageScale, AreaDetailReviewRangeDTO adrrDTO) {
		int endNum = 0;

		endNum = adrrDTO.getStartNum() + pageScale - 1;
		adrrDTO.setEndNum(endNum);

		return endNum;
	}// endNum
	
	/********************************* pagination ************************************/
	
}// class