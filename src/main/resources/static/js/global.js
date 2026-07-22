document.addEventListener("DOMContentLoaded", function () {

	// =====================
	// ★ Voteボタンの制御
	// =====================
	function showWarningToast(message) {
		const container = document.getElementById('toast-container');
		const toast = document.createElement('div');
		toast.classList.add('toast-body');
		toast.textContent = message;
		container.appendChild(toast);
		setTimeout(() => toast.remove(), 2500);
	}

	document.querySelectorAll('[data-is-own="true"]').forEach(btn => btn.classList.add('own-post-btn'));

	// 1. ハッシュタグ自動リンク処理
	const postElements = document.querySelectorAll('.post-content-text p');
	postElements.forEach(function (element) {
		const text = element.textContent;
		const hashtagRegex = /[#＃][A-Za-z0-9ぁ-んァ-ヶ一-龠ー_]+/g;
		if (!hashtagRegex.test(text)) return;

		element.innerHTML = text.replace(hashtagRegex, function (match) {
			const tagName = match.substring(1).toLowerCase();
			return `<a href="/tags/${tagName}" class="hashtag-text">${match}</a>`;
		});
	});

	// ======================
	// ❤️ いいねボタンの非同期処理
	// ======================
	document.querySelectorAll('.like-button').forEach(button => {
		button.addEventListener('click', async (e) => {
			const btn = e.currentTarget;
			const billId = btn.getAttribute('data-bill-id');
			btn.disabled = true;

			try {
				// 新設した LikeApiController を叩く
				const response = await fetch(`/api/bills/${billId}/like`, {
					method: 'POST'
				});

				if (response.ok) {
					const result = await response.json(); // { likes: X, liked: true/false }

					const countSpan = btn.querySelector('.like-count');
					const iconSpan = btn.querySelector('.like-icon');

					// 数字を最近に書き換え
					countSpan.textContent = result.likes;

					// 状態に応じてハートとクラスをトグル
					if (result.liked) {
						iconSpan.textContent = '❤️';
						btn.classList.add('liked');
					} else {
						iconSpan.textContent = '🖤';
						btn.classList.remove('liked');
					}
				}
			} catch (error) {
				console.error('いいねの非同期通信に失敗しました:', error);
			} finally {
				btn.disabled = false;
			}
		});
	});

	// =====================
	// ★ Voteボタンの非同期処理
	// =====================
	document.querySelectorAll('.vote-button').forEach(button => {
		button.addEventListener('click', async (e) => {
			const btn = e.currentTarget;

			// 自分の投稿なら通信させずにトーストを出して終了
			if (btn.getAttribute('data-is-own') === 'true') {
				showWarningToast('🏛️ 自身の提出法案に「投票」することはできません');
				return;
			}

			const billId = btn.getAttribute('data-bill-id');
			btn.disabled = true;

			try {
				const response = await fetch(`/api/bills/${billId}/vote`, {
					method: 'POST'
				});

				if (response.ok) {
					const result = await response.json();
					const icon = btn.querySelector('.vote-icon');
					const countSpan = btn.querySelector('.vote-count');

					countSpan.textContent = result.voteCount;

					if (result.voted) {
						icon.textContent = '★';
						btn.classList.add('voted');
					} else {
						icon.textContent = '☆';
						btn.classList.remove('voted');
					}
				}
			} catch (error) {
				console.error('Voteの非同期通信に失敗しました:', error);
			} finally {
				btn.disabled = false;
			}
		});
	});

});