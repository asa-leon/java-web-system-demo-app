package com.example.demo.service;

import com.example.demo.model.Bill;
import com.example.demo.model.Tag;
import com.example.demo.repository.BillRepository;
import com.example.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BillService {
	
	private final BillRepository billRepository;
	private final TagRepository tagRepository;

	/**
	 * 法案を作成・保存し、本文（description）からハッシュタグを抽出して中間テーブルにも保存する
	 */
	@Transactional
	public Bill createBill(Bill bill) {
		// 本文からハッシュタグを抽出してエンティティにセット
		extractAndAttachTags(bill);

		return billRepository.save(bill);
	}

	/**
	 * 本文からハッシュタグ（#〇〇）を抽出してBillに紐づける内部メソッド
	 */
	private void extractAndAttachTags(Bill bill) {
		String description = bill.getDescription();
		List<Tag> tagList = new ArrayList<>();

		if (description != null && !description.isEmpty()) {
			// 正規表現で「#」から始まる単語を抽出
			Pattern pattern = Pattern.compile("[#＃][A-Za-z0-9ぁ-んァ-ヶ一-龠ー_]+");
			Matcher matcher = pattern.matcher(description);

			while (matcher.find()) {
				// 先頭の「#」を消して小文字化
				String tagName = matcher.group().substring(1).toLowerCase();

				// 既存のタグがあれば取得、無ければ新規保存
				Tag tag = tagRepository.findByName(tagName)
						.orElseGet(() -> {
							Tag newTag = new Tag();
							newTag.setName(tagName);
							
							return tagRepository.save(newTag);
						});
				
				if (!tagList.contains(tag)) {
					tagList.add(tag);
				}
			}
		}

		// 抽出したタグのリストをセット（これで、カスケード等により中間テーブルへ保存される）
		bill.getTags().addAll(tagList);
	}
}