package com.example.demo.api;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * SPAのサンプルプログラム（CRUD形式）から呼び出されるRESTコントローラ。
 */
// CHECKSTYLE.OFF: MagicNumber
@RestController
@RequestMapping(path = "/api/crud")
public class CrudController {

	/** 商品リスト。 */
	private List<Item> items = new ArrayList<>();

	/**
	 * コンストラクタ。
	 */
	public CrudController() {
		items.add(new Item("001", "リポーター", "トンボ鉛筆", 350, null));
		items.add(new Item("002", "エアプレス", "トンボ鉛筆", 600, null));
		items.add(new Item("003", "モノグラフ", "トンボ鉛筆", 400, null));
		items.add(new Item("004", "モノグラフライト", "トンボ鉛筆", 200, null));
	}

	/**
	 * 商品を検索する。
	 * 
	 * @param name 商品名
	 * @param maker メーカー
	 * @return 商品リスト
	 * @throws Exception 例外
	 */
	@GetMapping
	public List<Item> search(@RequestParam(name = "name", required = false) @Nullable String name,
					@RequestParam(name = "maker", required = false) @Nullable String maker) throws Exception {
		Thread.sleep(2000);
		return items;
	}

	/**
	 * 商品を取得する。
	 *
	 * @param id 商品ID
	 * @return 商品
	 */
	@GetMapping("/{id}")
	public Item get(@PathVariable("id") String id) {
		return items.stream().filter(it -> it.id.equals(id)).findFirst().orElseThrow();
	}

	/**
	 * 商品を更新する。
	 *
	 * @param id 商品ID
	 * @param updateItem 更新する商品
	 * @return 更新後の商品
	 */
	@PutMapping("/{id}")
	public Item update(@PathVariable("id") String id, @RequestBody Item updateItem) {
		if (!id.equals(updateItem.id)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		// 同じidのプロダクトを見つけて差し替える
		for (int i = 0; i < items.size(); ++i) {
			var item = items.get(i);
			if (item.id.equals(updateItem.id)) {
				items.set(i, updateItem);
				return updateItem;
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

	/**
	 * 商品情報。
	 * 
	 * @param id 商品ID
	 * @param name 商品名
	 * @param maker メーカー
	 * @param price 価格
	 * @param description 商品説明
	 */
	record Item(String id, String name, String maker, int price, @Nullable String description) {

	}

}
