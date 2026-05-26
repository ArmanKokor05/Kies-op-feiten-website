import type { Router } from 'vue-router'

export function ToggleItems(router: Router, panelId: string) {
  const currentRoute = router.currentRoute.value;
  const currentPanels = currentRoute.params.panels;

  let panelArray: string[] = []
  if (Array.isArray(currentPanels)) {
    panelArray = [...currentPanels]
    console.log("Panel array filled", panelArray)
  } else if (currentPanels) {
    panelArray = [currentPanels]
    console.log("Panel array filled", panelArray)
  }

const index = panelArray.indexOf(panelId);
  console.log("index", index);
  if (index > -1) {
    panelArray.splice(index, 1)
    console.log("Panel removed ", panelArray, "at index", index)
  } else {
    panelArray.push(panelId);
    console.log("Panel array changed", panelArray)
  }

  let newPath: string;
  if (panelArray.length > 0) {
    newPath = `/${panelArray.join('/')}`;
    console.log("path made", newPath);
  } else {
    newPath = '/';
    console.log("path made", newPath);
  }

router.push(newPath);
  console.log("route pushed: ", newPath)
}
