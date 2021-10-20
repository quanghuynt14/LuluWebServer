var scene, camera, renderer, clock, mixer, actions, anims;

init();

function init() {
  actions = [];
  const assetPath = "https://s3-us-west-2.amazonaws.com/s.cdpn.io/2666677/";
  console.log("e");
  clock = new THREE.Clock();

  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x00aaff);

  camera = new THREE.PerspectiveCamera(
    60,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
  );
  camera.position.set(-1, 50, 250);

  const ambient = new THREE.AmbientLight(0xffffff, 1);
  scene.add(ambient);

  const light = new THREE.DirectionalLight(0xffffff, 2);
  light.position.set(9999, -9999, 1);
  scene.add(light);

  renderer = new THREE.WebGLRenderer();
  renderer.setSize(window.innerWidth, window.innerHeight);
  document.body.appendChild(renderer.domElement);

  const controls = new THREE.OrbitControls(camera, renderer.domElement);
  controls.target.set(1, 70, 0);
  controls.update();

  const map = new THREE.TextureLoader().load(
    "ee.jpeg",
    undefined,
    undefined,
    (err) => {
      console.log(err);
    }
  );
  const geometry = new THREE.BoxGeometry(33, 33, 33);
  const material1 = new THREE.MeshPhongMaterial({
    color: 0xffffff,
    map: map,
  });
  const meshCarre = new THREE.Mesh(geometry, material1);
  scene.add(meshCarre);

  //Add button actions here

  const loader = new THREE.FBXLoader();
  loader.load(
    "./Sackboy2.fbx",
    (object) => {
      console.log(object);
      mixer = new THREE.AnimationMixer(object);
      const action = mixer.clipAction(object.animations[0]);
      action.play();
      actions.push(action);
      object.traverse((child) => {
        if (child.name === "rdmobj00001" && child.isMesh) {
          const color_white = new THREE.Color(0xffffff);
          const color_dark_grey = new THREE.Color(0x111111);
          child.material.color = color_white;
          //child.material.specular = color_dark_grey;
        }
      });
      scene.add(object);

      update();
    },
    (error) => {
      console.log(error);
    },
    (error) => {
      console.log(error);
    }
  );

  window.addEventListener("resize", resize, false);
}

function playAction(index) {
  const action = actions[index];
  mixer.stopAllAction();
  action.reset();
  action.fadeIn(0.5);
  action.play();
}

function loadAnimation(loader) {
  const anim = anims.shift();

  loader.load(`Knight-anim-${anim}.fbx`, (object) => {
    const action = mixer.clipAction(object.animations[0]);
    if (anim == "die") {
      action.loop = THREE.LoopOnce;
      action.clampWhenFinished = true;
    }
    actions.push(action);
    if (anims.length > 0) {
      loadAnimation(loader);
    } else {
      update();
    }
  });
}

function update() {
  requestAnimationFrame(update);
  renderer.render(scene, camera);
  const dt = clock.getDelta();
  mixer.update(dt);
}

function resize() {
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(window.innerWidth, window.innerHeight);
}
