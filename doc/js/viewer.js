async function loadProject() {
  const params = new URLSearchParams(window.location.search);
  const file = params.get("project");

  const config = await fetch("projects/" + file).then(r => r.json());

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(60, window.innerWidth/window.innerHeight, 0.1, 1000);
  const renderer = new THREE.WebGLRenderer({ canvas: document.getElementById("viewer") });

  renderer.setSize(window.innerWidth, window.innerHeight);
  camera.position.set(config.camera.x, config.camera.y, config.camera.z);

  const light = new THREE.DirectionalLight(0xffffff, 1);
  light.position.set(5, 10, 7);
  scene.add(light);

  const loader = new THREE.GLTFLoader();
  loader.load(config.modelUrl, gltf => {
    scene.add(gltf.scene);
    animate();
  });

  function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
  }
}

loadProject();
