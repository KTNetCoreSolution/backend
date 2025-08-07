package com.boot.ktn.service.auth;

import com.boot.ktn.entity.auth.AuthMenuEntity;
import com.boot.ktn.mapper.auth.AuthMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthMenuService {
    private final AuthMenuMapper authMenuMapper;

    public List<Map<String, Object>> getMenuTree(String userId) {
        // 메뉴 데이터 조회
        List<AuthMenuEntity> menuEntities = authMenuMapper.findByMenu(userId);
        if (menuEntities == null || menuEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // 트리 구조로 변환
        return convertToMenuTree(menuEntities);
    }

    private List<Map<String, Object>> convertToMenuTree(List<AuthMenuEntity> menuEntities) {
        // menuId -> node 맵
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        Map<String, Integer> orderMap = new HashMap<>(); // 정렬용 별도 맵

        // 1. 모든 메뉴를 노드로 변환
        for (AuthMenuEntity menu : menuEntities) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("MENUID", menu.getMenuId());
            node.put("MENUNM", menu.getMenuNm());
            node.put("URL", menu.getUrl() != null ? menu.getUrl() : "");
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(menu.getMenuId(), node);

            // menuOrder 변환 (null 또는 비숫자일 경우 0으로 처리)
            int order = 0;
            try {
                if (menu.getMenuOrder() != null) {
                    order = Integer.parseInt(menu.getMenuOrder().toString());
                }
            } catch (NumberFormatException e) {
                order = 0;
            }
            orderMap.put(menu.getMenuId(), order);
        }

        // 최상위 메뉴 리스트
        List<Map<String, Object>> tree = new ArrayList<>();

        // 2. 부모-자식 관계 연결
        for (AuthMenuEntity menu : menuEntities) {
            Map<String, Object> currentNode = nodeMap.get(menu.getMenuId());
            if (menu.getUpperMenuId() == null || menu.getUpperMenuId().isEmpty()) {
                tree.add(currentNode);
            } else {
                Map<String, Object> parentNode = nodeMap.get(menu.getUpperMenuId());
                if (parentNode != null) {
                    ((List<Map<String, Object>>) parentNode.get("children")).add(currentNode);
                }
            }
        }

        // 3. 정렬 재귀 적용
        sortMenuList(tree, orderMap);

        return tree;
    }

    @SuppressWarnings("unchecked")
    private void sortMenuList(List<Map<String, Object>> menuList, Map<String, Integer> orderMap) {
        // menuOrder 기준 정렬
        menuList.sort(Comparator.comparingInt(m -> orderMap.getOrDefault((String) m.get("MENUID"), 0)));

        // children 재귀 정렬
        for (Map<String, Object> menu : menuList) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) menu.get("children");
            if (children != null && !children.isEmpty()) {
                sortMenuList(children, orderMap);
            }
        }
    }

}
