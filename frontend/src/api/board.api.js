import { http, httpFile } from "./http";

// 게시글 목록 조회
// GET /api/boards
export const fetchBoardList = async (params) => {
  try {
    const response = await http.get("/boards", { params }); // /board -> /boards
    return response.data;
  } catch (error) {
    console.error("Failed to fetch board list:", error);
    throw error;
  }
};

// 게시글 상세 조회
// GET /api/boards/{boardNo}
export const fetchBoardDetail = async (boardNo) => {
  try {
    const response = await http.get(`/boards/${boardNo}`); // /board -> /boards
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch board detail (${boardNo}):`, error);
    throw error;
  }
};

// 게시글 작성 (관리자용)
// POST /api/boards
export const createBoard = async (boardData) => {
  try {
    const response = await httpFile.post("/boards", boardData); // /board -> /boards
    return response.data;
  } catch (error) {
    console.error("Failed to create board:", error);
    throw error;
  }
};

// 게시글 수정 (관리자용)
// PATCH /api/boards/{boardNo}
export const updateBoard = async (boardNo, boardData) => {
  try {
    const response = await httpFile.patch(`/boards/${boardNo}`, boardData); // put -> patch, /board -> /boards
    return response.data;
  } catch (error) {
    console.error(`Failed to update board (${boardNo}):`, error);
    throw error;
  }
};

// 게시글 삭제 (관리자용)
// DELETE /api/boards/{boardNo}
export const deleteBoard = async (boardNo) => {
  try {
    const response = await http.delete(`/boards/${boardNo}`); // /board -> /boards
    return response.data;
  } catch (error) {
    console.error(`Failed to delete board (${boardNo}):`, error);
    throw error;
  }
};