//댓글 수정
function modifyComment(commentId) {  //수정 버튼을 누르면 수정할 수 있는 창을 보여줌
    document.getElementById(`comment-contents-${commentId}`).style.display = "none";
    document.getElementById(`modify-div-${commentId}`).style.display = "block";
}

function cancel(commentId) {  //취소 버튼을 누르면 수정 창을 닫음
    document.getElementById(`comment-contents-${commentId}`).style.display = "block";
    document.getElementById(`modify-div-${commentId}`).style.display = "none";
}

function commentModify(commentId) {
    const postId = document.getElementById("postId").value;
    const content = document.getElementById(`comment-modify-${commentId}`).value;

    fetch(`/comment/revise/${commentId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            contents: content
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorMessage => {
                throw new Error(errorMessage); // 서버의 에러 메시지를 에러로 전달
            });
        }
        alert("수정 완료되었습니다.");
        location.replace("/select/Id/" + postId);
    })
    .catch(error => {
        alert(error.message); // 에러 메시지를 alert에 출력
        location.replace("/select/Id/" + postId);
    });
}

//댓글 삭제
function commentDelete(commentId) {
    //삭제 버튼을 클릭하면 /delete/{id}로 요청을 보냄
    if (confirm('이 댓글을 삭제하시겠습니까?')) {
        const postId = document.getElementById("postId").value;

        fetch(`/comment/delete/${commentId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage); // 서버의 에러 메시지를 에러로 전달
                });
            }
            alert("삭제 완료되었습니다.");
            location.replace("/select/Id/" + postId);
        })
        .catch(error => {
            alert("삭제 실패: " + error.message); // 에러 메시지를 alert에 출력
            location.replace("/select/Id/" + postId);
        });
    }
}

//댓글 추천 or 비추천
function commentrecommend(postId, commentId, likes) {
    const url = "/comment/like/" + commentId;
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(likes)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorMessage => {
                throw new Error(errorMessage); // 서버의 에러 메시지를 에러로 전달
            });
        }
        location.replace("/select/Id/" + postId);
    })
    .catch(error => {
        alert(error.message); // 에러 메시지를 alert에 출력
        location.replace("/select/Id/" + postId);
    });
}