//수정 기능
//id가 modify-btn인 엘리먼트 조회
const modifyButton = document.getElementById('modify-btn');

if(modifyButton) {
    //클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', event => {
        const id = document.getElementById("board-id").value;

        body = JSON.stringify({
            title: document.getElementById("title").value,
            contents: document.getElementById("contents").value
        })

        function success() {
            alert("수정 완료되었습니다");
            location.replace("/select/Id/" + id);
        }

        function fail() {
            alert("수정 실패했습니다.");
            location.replace("/select/Id/" + id);
        }

        httpRequest("PUT", "/revise/" + id, body, success, fail);
    });
}

//등록 기능
//id가 create-btn인 엘리먼트 조회
const createButton = document.getElementById('create-btn');

if(createButton) {
    //등록 버튼을 클릭하면 /post로 요청을 보냄
    createButton.addEventListener("click", event => {
        body = JSON.stringify({
            title: document.getElementById("title").value,
            contents: document.getElementById("contents").value
        });
        function success() {
            alert("등록 완료되었습니다.");
            location.replace("/select");
        };
        function fail() {
            alert("등록 실패했습니다.");
            location.replace("/select");
        };

        httpRequest("POST", `/post`, body, success, fail)
    });
}

//삭제 기능
//id가 delete-btn인 엘리먼트 조회
const deleteButton = document.getElementById("delete-btn");

if(deleteButton) {
    //삭제 버튼을 클릭하면 /delete/{id}로 요청을 보냄
    deleteButton.addEventListener("click", event => {
        if (confirm('이 게시글을 삭제하시겠습니까?')) {
            const id = document.getElementById("postId").value;

            fetch(`/delete/${id}`, {
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
                location.replace("/select");
            })
            .catch(error => {
                alert("삭제 실패: " + error.message); // 에러 메시지를 alert에 출력
                location.replace("/select/Id/" + id);
            });
        }
    });
}

//게시글 추천 or 비추천
function recommend(postId, likes) {
    const url = "/like/" + postId;
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

// 로그아웃 기능
const logoutButton = document.getElementById('logout-btn');

if (logoutButton) {
    logoutButton.addEventListener('click', event => {
        function success() {
            // 로컬 스토리지에 저장된 액세스 토큰을 삭제
            localStorage.removeItem('access_token');

            // 쿠키에 저장된 리프레시 토큰을 삭제
            deleteCookie('refresh_token');
            location.replace('/login');
        }
        function fail() {
            alert('로그아웃 실패했습니다.');
        }

        httpRequest('DELETE','/api/refresh-token', null, success, fail);
    });
}

// 쿠키를 삭제하는 함수
function deleteCookie(name) {
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

/* POST 요청을 보낼 때 엑세스 토큰도 함께 보냄.
만약 응답에 권한이 없다는 에러 코드가 발생하면 리프레시 토큰과 함께 새로운 액세스 토큰 요청.
전달받은 액세스 토큰으로 다시 API 요청함.*/

//쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(";");
    cookie.some(function (item) {
        item = item.replace(" ", "");

        var dic = item.split("=");

        if(key == dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

//HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            //로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: "Bearer " + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body: body,
    }).then((response) => {
        if(response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie("refresh_token");
        if(response.status === 401 && refresh_token) {
            fetch("/api/token", {
                method: 'POST',
                headers: {
                    Authorization: "Bearer" + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: getCookie("refresh_token"),
                }),
            })
                .then((res) => {
                    if(res.ok) {
                        return res.json();
                    }
                })
                .then((result) => {
                    //재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem("access_token", result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch((error) => fail());
        } else {
            return fail();
        }
    });
}

//검색 기능
function search() {
    const keyword = document.getElementById("keyword").value.trim();

    if (keyword === "") {
        alert("키워드를 입력해주세요.");
        return;
    }

    const searchType = document.getElementById("searchType").value;
    // `/search`로 GET 요청 보내기
    window.location.href = `/search?type=${searchType}&keyword=${encodeURIComponent(keyword)}`;
}